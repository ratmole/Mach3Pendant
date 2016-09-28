package gr.ratmole.android.Mach3Pendant.shared;

import java.util.ArrayList;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

// This class is a convenient place to keep things common to both the client and server.
public class Network {
    static public final int TCP_PORT = 54555;
    static public final int UDP_PORT = 54777;

    // This registers objects that are going to be sent over the network.
    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(String[].class);
        kryo.register(SomeRequest.class);
        kryo.register(SomeResponse.class);
        kryo.register(KeyEvent.class);
        kryo.register(EventSequence.class);
        kryo.register(Event.class);
        kryo.register(ArrayList.class);
        kryo.register(ServerGreeting.class);
        kryo.register(ServerWindow.class);
        kryo.register(Handshake.class);
    }

    static public class SomeRequest {
        public String text;
    }

    static public class SomeResponse {
        public String text;
    }

}