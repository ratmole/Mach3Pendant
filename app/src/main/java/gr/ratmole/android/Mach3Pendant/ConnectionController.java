package gr.ratmole.android.Mach3Pendant;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import gr.ratmole.android.Mach3Pendant.actionbar.ActionBarHelper;
import gr.ratmole.android.Mach3Pendant.fragments.OnConnectionActionPerformedListener;
import gr.ratmole.android.Mach3Pendant.utils.Log;

public class ConnectionController extends Fragment {
    private FragmentManager fragmentManager;

    private static final long CONNECTION_TIMEOUT_IN_S = 300;
    private static final String FRAGMENT_CONNECTION = "connection_in_progress";

    private ConnectivityManager connManager;
    private boolean isDestroying = false;
    private OnConnectionActionPerformedListener actionPerformedListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        Mach3PendantApplication application = (Mach3PendantApplication) getActivity().getApplication();
        connManager = application.getConnectivityManager();
        connManager.activate();

        connManager.setOnConnectionChangeListener(new ConnectivityManager.OnConnectionChangeListener() {
            public void onConnect(String osName_, String osVersion_, String userName_, boolean approvedByUser_, int piServerVersion_) {
                onConnected();
            }

            public void onReconnect() {

            }

            public void onDisconnect() {
                onDisconnected();
            }

            public void onPinFailed() {
                actionPerformedListener.onPinInserted(false);
            }

            public void onEnterPin() {
                actionPerformedListener.onPinInserted(true);
            }
        });

        abReset();

        //Add temporary connection fragment
        if (fragmentManager.findFragmentByTag(FRAGMENT_CONNECTION) == null) {
            actionPerformedListener.onConnectionAbsence();
        }
    }

    private void onConnected() {
        abSetRefresh(false);
        ((ActionBarActivity) getActivity()).getActionBarHelper().setRefreshActionIcon(R.drawable.action_wifi);
        actionPerformedListener.onConnected();
    }

    @Override
    public void onStart() {
        super.onStart();
        isDestroying = false;

        /**
         * It's depends from menu creation.
         * But menu creation invoke before onCreate.
         * So we display progress bar first time by this way
         */
        if (!connManager.isConnected()) {
            abSetRefresh(true);
        }
    }

    private void onDisconnected() {
        Log.d("onDisconnected");
        if (isDestroying) return;
        abReset();
        actionPerformedListener.onDisconnect();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroying = true;
        //Disconnect
        new Thread() {
            public void run() {
                connManager.deactivate();
            }
        }.start();

    }

    private void abReset() {
        getActivity().setTitle(getResources().getString(R.string.app_name));
        abSetRefresh(true);
    }

    private void abSetRefresh(boolean refresh) {
        ActionBarHelper abHelper = ((ActionBarActivity) getActivity()).getActionBarHelper();
        if (abHelper != null) {
            abHelper.setRefreshActionItemState(refresh);
        }
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            actionPerformedListener = (OnConnectionActionPerformedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implemented OnConnectionActionPerformedListener");
        }
    }
}
