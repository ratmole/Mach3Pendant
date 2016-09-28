package gr.ratmole.android.Mach3Pendant;

import android.os.Handler;
import android.os.Message;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import gr.ratmole.android.Mach3Pendant.shared.Handshake;
import gr.ratmole.android.Mach3Pendant.shared.Network;
import gr.ratmole.android.Mach3Pendant.shared.ServerGreeting;
import gr.ratmole.android.Mach3Pendant.shared.ServerWindow;
import gr.ratmole.android.Mach3Pendant.utils.Log;

import java.io.IOException;
import java.net.InetAddress;

public class ConnectivityManager {

    private static final int SERVER_GREETING = 0;
    private static final int SERVER_WINDOW = 1;
    private static final int SERVER_DISCONNECTED = 2;
    private static final int ENTER_PIN = 3;
    private static final int INCORRECT_PIN = 4;

    //Handler
    private ConnectionEventHandler eventHandler = new ConnectionEventHandler();
    //Network related listeners
    private DataInterchangeListener dataListener = new DataInterchangeListener();
    private HandshakeListener handshakeListener = new HandshakeListener();
    //UI related listeners
    private OnConnectionChangeListener onConnectionChangeListener;
    private OnChangeWindowListener onChangeWindowListener;

    private Connection trustedConnection = null;

    private Client udpClient, tcpClient;
    private boolean stopping = false;
    private Connection pinConnection = null;
    //Need for credentials
    private String accountName;
    private String accountType;

    public ConnectivityManager(String accountName_, String accountType_) {
        this.accountName = accountName_;
        this.accountType = accountType_;
        tcpClient = new Client();
        udpClient = new Client();
        Network.register(tcpClient);

        setListeners();
    }

    private void setListeners() {
      tcpClient.addListener(handshakeListener);
    }



    public void activate() {
        tcpClient.start();
        udpClient.start();
        new ConnectionThread().start();
        Log.d("activated");
    }

    public void deactivate() {
        stopping = true;
        tcpClient.stop();
        udpClient.stop();
        if (trustedConnection != null) trustedConnection.close();
        Log.d("deactivate");
    }

    public void sendMessage(final Object msg) {
        if (isConnected() && msg != null) {
            new Thread() {
                public void run() {
                    trustedConnection.sendTCP(msg);
                }
            }.start();

        } else {
            Log.e("Message is null and wasn't sent");
        }
    }

    public void sendPin(final String pinValue, final boolean remember) {
        if (pinConnection != null) {
            new Thread() {
                public void run() {
                    pinConnection.sendTCP(new Handshake().phone_SentPin(pinValue, remember));
                }
            }.start();
        }
    }

    public void setOnConnectionChangeListener(OnConnectionChangeListener onConnectionChangeListener_) {
        onConnectionChangeListener = onConnectionChangeListener_;
    }

    public void setOnChangeWindowListener(OnChangeWindowListener onChangeWindowListener_) {
        onChangeWindowListener = onChangeWindowListener_;
    }

    public boolean isConnected() {
        return trustedConnection != null &&
                trustedConnection.isConnected();
    }

    private class ConnectionEventHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SERVER_DISCONNECTED:
                    //To reset the previous state
                    notifyOnChangeListener("");
                    if (onConnectionChangeListener != null) onConnectionChangeListener.onDisconnect();
                    break;
                case SERVER_GREETING:
                    ServerGreeting greeting = (ServerGreeting) msg.obj;
                    if (onConnectionChangeListener != null) {
                        onConnectionChangeListener.onConnect(greeting.osName, greeting.osVersion, greeting.userName, greeting.approvedByUser, greeting.Mach3PendantVersion);
                    }
                    notifyOnChangeListener(greeting.serverWindow.getProcessName());
                    break;
                case SERVER_WINDOW:
                    ServerWindow window = (ServerWindow) msg.obj;
                    notifyOnChangeListener(window.getProcessName());
                    break;
                case ENTER_PIN:
                    if (onConnectionChangeListener != null) {
                        onConnectionChangeListener.onEnterPin();
                    }
                    break;
                case INCORRECT_PIN:
                    if (onConnectionChangeListener != null) {
                        onConnectionChangeListener.onPinFailed();
                    }
                default:
                    break;
            }
        }

        private void notifyOnChangeListener(String procName) {
            if (onChangeWindowListener != null) {
                onChangeWindowListener.onChange(procName);
            }
        }
    }

    private class DataInterchangeListener extends Listener {
        @Override
        public void received(Connection connection, Object object) {
            if (connection != trustedConnection) return;
            //CHANGE WINDOW message
            if (object instanceof ServerWindow) {
                Log.d("Server change window received");
                ServerWindow window = (ServerWindow) object;
                Message msg = eventHandler.obtainMessage(SERVER_WINDOW, window);
                eventHandler.sendMessage(msg);
            }
        }

        @Override
        public void disconnected(Connection connection) {
            if (connection != trustedConnection) return;
            Log.d("Server disconnect received");

            trustedConnection = null;

            Message msg = eventHandler.obtainMessage(SERVER_DISCONNECTED);
            eventHandler.sendMessage(msg);

            //If not stopping, should to make reconnect
            if (!stopping) new ConnectionThread().start();
        }
    }

    private class HandshakeListener extends Listener {
        @Override
        public void received(Connection connection, Object object) {
            //HANDSHAKE process
            if (object instanceof Handshake) {
                Handshake h = (Handshake) object;
                switch (h.getId()) {
                    case Handshake.PC_ENTER_PIN:
                        Log.d("Received PC:ENTER_PIN");
                        //connection.sendTCP(new Handshake().phone_SentPin("1234"));
                        pinConnection = connection;
                        eventHandler.sendMessage(eventHandler.obtainMessage(ENTER_PIN));
                        break;
                    case Handshake.PC_INCORRECT_PIN:
                        Log.d("Received PC:INCORRECT_PIN");
                        eventHandler.sendMessage(eventHandler.obtainMessage(INCORRECT_PIN));
                        break;
                    case Handshake.PC_I_TRUST_PHONE:
                        Log.d("Received PC:I_TRUST_PHONE");
                        //We can trust to this connection
                        trustedConnection = connection;
                        break;
                    default:
                        break;
                }
            }
            //GREETING message
            else if (object instanceof ServerGreeting && connection == trustedConnection) {
                Log.d("Server greeting received");
                //It's the start point to receive PC messages in normal mode
                trustedConnection.addListener(dataListener);

                ServerGreeting greeting = (ServerGreeting) object;
                Log.d("USER.NAME = " + greeting.userName);
                Message msg = eventHandler.obtainMessage(SERVER_GREETING, greeting);
                eventHandler.sendMessage(msg);
            }

        }
    }

    private class ConnectionThread extends Thread {
        @Override
        public void run() {
            trustedConnection = null;
            while (!stopping) {
                /**
                 * Discover server via WiFi
                 */
                InetAddress address = udpClient.discoverHost(Network.UDP_PORT, 5000);
                Log.d("Discovered server address:" + address);

                /**
                 * Server discovered via WiFi? Try to use it.
                 */
                if (address != null) {
                    try {
                        tcpClient.start();
                        tcpClient.connect(5000, address, Network.TCP_PORT);
                        tcpClient.sendTCP(new Handshake().phone_TrustMe(accountName, accountType));
                        Log.d("Connect via WiFI..");
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    interface OnChangeWindowListener {
        void onChange(String procName);
    }

    interface OnConnectionChangeListener {
        void onConnect(String osName_, String osVersion_, String userName_, boolean approvedByUser_, int Mach3PendantServerVersion);

        void onEnterPin();

        void onReconnect();

        void onDisconnect();

        void onPinFailed();
    }

}
