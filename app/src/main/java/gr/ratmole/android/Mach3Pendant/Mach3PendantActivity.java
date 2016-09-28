package gr.ratmole.android.Mach3Pendant;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.*;
import android.widget.TextView;

import gr.ratmole.android.Mach3Pendant.fragments.*;
import gr.ratmole.android.Mach3Pendant.model.Application;
import gr.ratmole.android.Mach3Pendant.model.Hotkeys;
import gr.ratmole.android.Mach3Pendant.utils.Log;

public class Mach3PendantActivity extends ActionBarActivity implements OnConnectionActionPerformedListener {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private PowerManager.WakeLock wakeLock;
    private Hotkeys hotkeys;

    private ConnectionController connectionController;
    private OsLevelController touchController;
    private GridFragment gridFragment;
    private ConnectionFragment connectionFragment;


    private ConnectivityManager connManager;
    private PinInputFragment pinFragment;
    private String prevProcName = "";
    private static final String FRAGMENT_PIN = "pin";
    private static final String FRAGMENT_GRID = "grid";

    private static final int DIALOG_EDIT_LAYOUT = 3;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //here you can handle orientation change
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Creating Mach3Pendant activity...");
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);

        //Keep screen on
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "DimScreen");

        //Create the connectivity manager with credentials (user name and type of account)
        createConnManager();

        setOnWindowChangeListeners();

        //Load hotkeys configuration from XML
        try {
            hotkeys = new Hotkeys(this);
        } catch (Exception e) {
            Log.e(e.toString());
        }
        // Init needed fragments
        touchController = new OsLevelController(hotkeys);
        gridFragment = new GridFragment(hotkeys);
        connectionFragment = new ConnectionFragment();
        pinFragment = new PinInputFragment();
        connectionController = new ConnectionController();

        //Add invisible fragments - controllers
        fragmentManager.beginTransaction().
                add(touchController, "touch").
                add(connectionController, "connection_controller").
                commit();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //We wait response? Then menu item must be not enabled
        //We need do this here due the Honeycomb specific menu
        //getActionBarHelper().setRefreshActionItemState(isWaitResponse());
        return super.onPrepareOptionsMenu(menu);
    }

    private void createConnManager() {
        String accountName, accountType;
        Account[] accounts = AccountManager.get(this).getAccounts();
        if (accounts.length != 0) {
            accountName = accounts[0].name;
            accountType = accounts[0].type;
        } else {
            accountName = "Unnamed";
            accountType = "Unnamed";
        }
        connManager = new ConnectivityManager(accountName, accountType);
        Mach3PendantApplication application = (Mach3PendantApplication) getApplication();
        application.setConnectivityManager(connManager);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Mach3Pendant activity has been started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();
        Log.d("Mach3Pendant activity has been resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        wakeLock.release();
        Log.d("Mach3Pendant activity has been paused");
        this.finish();
        return;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Mach3Pendant activity has been stopped");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Finishing Mach3Pendant activity...");
    }


    private void setOnWindowChangeListeners() {
        connManager.setOnChangeWindowListener(new ConnectivityManager.OnChangeWindowListener() {
            public void onChange(String procName) {
                processWindowChange(procName);
            }
        });
    }

    private void processWindowChange(String procName) {
        Log.d("Active window changed to: " + procName);
        //Are really active pc app has been changed?
        if (reallyProcNameChanged(procName)) {
            //If active pc app supported
            if (hotkeys.isSupportedApp(procName)) {
                //activeApp can be null. In this case PC app not supported;
                Application activeApp = hotkeys.getActiveApp();
                if (gridFragment != null) gridFragment.setActiveApp(activeApp);
                CharSequence appName = activeApp == null ? "" : activeApp.name;
                setTitle(appName);
            }
            //Show only proc name and set a blank fragment
            else {
                setTitle("Mach3 CNC Window Focus Lost");
                setBlankFragmentInsteadOfActiveApp();
            }
        }
    }

    private boolean reallyProcNameChanged(String procName_) {
        if (procName_.equalsIgnoreCase(prevProcName)) {
            return false;
        } else {
            prevProcName = procName_;
            return true;
        }
    }

    private void setBlankFragmentInsteadOfActiveApp() {
        if (gridFragment != null) gridFragment.setActiveApp(null);
    }

    private void showTitlesDialog() {

        String aboutTitle = String.format("About %s", getString(R.string.app_name));
        String versionString = String.format("Version: %s", getString(R.string.version));
        String aboutText = getString(R.string.about);

        final TextView message = new TextView(this);
        final SpannableString s = new SpannableString(aboutText);
        message.setPadding(5, 5, 5, 5);
        message.setText(versionString + "\n\n" + s);
        Linkify.addLinks(message, Linkify.ALL);

        new AlertDialog.Builder(this).
                setTitle(aboutTitle).
                setCancelable(true).
                setIcon(R.drawable.icon).
                setPositiveButton(this.getString(android.R.string.ok), null).
                setView(message).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        boolean ret = super.onCreateOptionsMenu(menu);
        return ret;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.menu_about:
                showTitlesDialog();
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private Dialog createDialog(int titleId, int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleId).setIcon(android.R.drawable.stat_sys_warning).setMessage(messageId).setCancelable(
                false).setPositiveButton(android.R.string.ok, null);
        return builder.create();
    }




    @Override
    public void onPinInserted(boolean flag) {
        if (flag) {
            fragmentManager.beginTransaction().replace(R.id.pad, pinFragment, FRAGMENT_PIN).commit();
        } else {
            pinFragment.incorrectPin();
        }
    }

    private static final String FRAGMENT_CONNECTION = "connection_in_progress";

    @Override
    public void onConnectionAbsence() {
        fragmentManager.beginTransaction().replace(R.id.pad, connectionFragment, FRAGMENT_CONNECTION).commitAllowingStateLoss();
    }

    @Override
    public void onConnected() {
        //Replace by real grid fragment
        if (fragmentManager.findFragmentByTag(FRAGMENT_GRID) == null) {
            fragmentManager.beginTransaction().replace(R.id.pad, gridFragment, FRAGMENT_GRID).commitAllowingStateLoss();
        }
    }

    @Override
    public void onDisconnect() {
        //Replace by real grid fragment
        if (fragmentManager.findFragmentByTag(FRAGMENT_CONNECTION) == null) {
            fragmentManager.beginTransaction().replace(R.id.pad, connectionFragment, FRAGMENT_CONNECTION).commitAllowingStateLoss();
        }
    }
}
