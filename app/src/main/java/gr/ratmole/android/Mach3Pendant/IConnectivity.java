package gr.ratmole.android.Mach3Pendant;

import com.esotericsoftware.kryonet.EndPoint;

public interface IConnectivity {
    EndPoint getEndpoint();

    boolean establishConnection();

    boolean isConnected();

    void closeConnection();

    void setUncaughtExceptionHand(Thread.UncaughtExceptionHandler handler);
}
