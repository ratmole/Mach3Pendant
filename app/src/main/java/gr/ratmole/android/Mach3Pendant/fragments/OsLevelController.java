package gr.ratmole.android.Mach3Pendant.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import gr.ratmole.android.Mach3Pendant.ConnectivityManager;
import gr.ratmole.android.Mach3Pendant.Mach3PendantApplication;
import gr.ratmole.android.Mach3Pendant.model.Hotkeys;
import gr.ratmole.android.Mach3Pendant.model.Key;
import gr.ratmole.android.Mach3Pendant.shared.EventSequence;
import gr.ratmole.android.Mach3Pendant.shared.KeyEvent;
import gr.ratmole.android.Mach3Pendant.utils.Log;

public class OsLevelController extends Fragment {
    private ConnectivityManager connManager;
    private Hotkeys hotkeys;

    private KeySendEngine keySendEngine = new KeySendEngine();


    public OsLevelController() {
        super();
    }

    @SuppressLint("ValidFragment")
    public OsLevelController(Hotkeys hotkeys_) {
        this.hotkeys = hotkeys_;
    }


    @Override
    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);
        Mach3PendantApplication application = (Mach3PendantApplication) getActivity().getApplication();
        connManager = application.getConnectivityManager();
    }


    private void sendKey(String id) {
        Key key = hotkeys.oskeys.get(id);
        if (key != null) {
            EventSequence msg = key.getEventSequence();
            connManager.sendMessage(msg);
        }
    }

    private class KeySendEngine extends Thread {
        //How much key will be sent per second
        double speed = 0;
        String keyId = "";
        private boolean stopNow = false;

        @Override
        public void run() {
            while (!stopNow) {
                if (speed == 0) continue;

                sendKey(keyId);
                try {
                    sleep((long) (1000 / speed));
                } catch (InterruptedException e) {
                    Log.e(e.toString());
                }
            }
        }

    }

}
