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

/**
 * Created by IntelliJ IDEA.
 * User: pilgr
 * Date: 13.06.11
 * Time: 22:37
 * To change this template use File | Settings | File Templates.
 */
public class OsLevelController extends Fragment {
    private ConnectivityManager connManager;
    private Hotkeys hotkeys;
    private boolean isScrollProcessing = false;

    private int downX;
    private int xTotal;

    private KeySendEngine keySendEngine = new KeySendEngine();

    private static final int DISABLE_AREA_SIZE = 50;
    private static final double SPEED_MULTIPLICATOR = 100.0;
    private float startScrollY0;
    private float startScrollY1;
    private int prevSegment;
    private long f0DownTime;
    private long f1DownTime;
    private long f1UpTime;
    private long f0UpTime;
    private static final long MAX_FINGER_DIF_TIME = 100;

    private static final String OSKEY_ALTTAB = "alttab";
    private static final String OSKEY_SWITCH_WINDOW_START = "wintab";
    private static final String OSKEY_SWITCH_WINDOW_NEXT = "tab";
    private static final String OSKEY_SWITCH_WINDOW_PREV = "shifttab";
    private static final String OSKEY_SWITCH_WINDOW_FINISH = "win";

    private enum Mode {
        VERTICAL_SCROLL, SWITCH_WINDOW, ENTER, FAIL, NONE
    }

    private Mode mode = Mode.NONE;

    /*
    Uses to divide the scroll delta to calculate the number of segment
     */
    private static final int SCROLL_SEGMENT_SIZE = 20;

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

    public boolean onTouch(MotionEvent ev) {
        int actionCode = ev.getAction() & MotionEvent.ACTION_MASK;
        //DumpEvents.dumpEvent(ev);


        //Touch time save
        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
                f0DownTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                f1DownTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                f1UpTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                f0UpTime = System.currentTimeMillis();
                break;
        }

        //The second finger was touched
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN && mode == Mode.NONE) {
            startScrollY0 = ev.getY(0);
            startScrollY1 = ev.getY(1);
            prevSegment = 0;
            mode = Mode.ENTER;
        }

        if (actionCode == MotionEvent.ACTION_MOVE && ev.getPointerCount() == 2 && mode != Mode.SWITCH_WINDOW) {
            float currY0 = ev.getY(0);
            float currY1 = ev.getY(1);
            float deltaY = ((startScrollY0 - currY0) + (startScrollY1

                    - currY1)) / 2;
            int segment = (int) deltaY / SCROLL_SEGMENT_SIZE;
            if (prevSegment != segment) {
                mode = Mode.VERTICAL_SCROLL;
                int deltaSegment = prevSegment - segment;
                Log.d("Vertical scroll up to = " + deltaSegment);
                prevSegment = segment;
                connManager.sendMessage(new EventSequence().wheel(segment));
            }

        }

        //Switch window
        if (actionCode == MotionEvent.ACTION_POINTER_UP &&
                mode != Mode.VERTICAL_SCROLL &&
                Math.abs(f0DownTime - f1DownTime) > MAX_FINGER_DIF_TIME) {
            int action = ev.getAction();
            int upPid = action >> MotionEvent.ACTION_POINTER_ID_SHIFT;

            float upX = ev.getX(upPid);
            float touchX = ev.getX(upPid == 0 ? 1 : 0);
            //Log.d("upX= " + upX + "touchX= " + touchX);
            if (mode != Mode.SWITCH_WINDOW) {
                sendKey(OSKEY_SWITCH_WINDOW_START);
            } else if (upX > touchX) {
                sendKey(OSKEY_SWITCH_WINDOW_NEXT);
            } else {
                sendKey(OSKEY_SWITCH_WINDOW_PREV);
            }
            mode = Mode.SWITCH_WINDOW;
        }

        if (actionCode == MotionEvent.ACTION_UP && mode == Mode.SWITCH_WINDOW) {
            sendKey(OSKEY_SWITCH_WINDOW_FINISH);
        }

        //If double-finger tap - send ENTER
        if (actionCode == MotionEvent.ACTION_UP && mode == Mode.ENTER) {
            //Filter
            long upDif = Math.abs(f0UpTime - f1UpTime);
            long downDif = Math.abs(f0DownTime - f1DownTime);
            //Log.d("upDif= " + upDif + " downDif= " + downDif);
            if (upDif < MAX_FINGER_DIF_TIME && downDif < MAX_FINGER_DIF_TIME) {
                connManager.sendMessage(new EventSequence().press(KeyEvent.VK_ENTER).release(KeyEvent.VK_ENTER));
                Log.d("Send ENTER");
            }
        }

        //Finish gesture processing
        if (actionCode == MotionEvent.ACTION_UP || actionCode == MotionEvent.ACTION_CANCEL) {
            //Set action to cancel and reset mode
            if (mode != Mode.NONE) {
                ev.setAction(MotionEvent.ACTION_CANCEL);
                mode = Mode.NONE;
            }
        }

        return mode != Mode.NONE;
    }

    public boolean processMoveEvent(MotionEvent ev) {
        xTotal = (int) (ev.getX() - downX);

        //up level for start scrolling
        if (Math.abs(xTotal) < DISABLE_AREA_SIZE / 2 && !isScrollProcessing) {
            Log.d("abs(xTotal) < 60 , ignore scroll event");
            return false;
        }

        if (Math.abs(xTotal) < DISABLE_AREA_SIZE / 2 && isScrollProcessing) {
            keySendEngine.setKeyAndSpeed(OSKEY_SWITCH_WINDOW_PREV, 0);
            Log.d("set zero speed");
            return true;
        }

        //First event of scroll processing
        if (!isScrollProcessing) {
            startScrollProcessing();
        }

        int total = Math.abs(xTotal);
        double speed = (total - DISABLE_AREA_SIZE / 2) / SPEED_MULTIPLICATOR + 0.5;
        Log.d("Current speed =" + speed);
        if (xTotal < 0) {
            keySendEngine.setKeyAndSpeed(OSKEY_SWITCH_WINDOW_PREV, speed);
        } else {
            keySendEngine.setKeyAndSpeed(OSKEY_SWITCH_WINDOW_NEXT, speed);
        }

        return true;
    }

    private void startScrollProcessing() {

        isScrollProcessing = true;
        Log.d("First processed scroll event");

        sendKey(OSKEY_SWITCH_WINDOW_START);
        keySendEngine = new KeySendEngine();
        keySendEngine.start();
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

        public void setKeyAndSpeed(String keyId_, double speed_) {
            speed = speed_;
            keyId = keyId_;
        }

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

        public void stopEngine() {
            stopNow = true;
        }
    }

}
