package gr.ratmole.android.Mach3Pendant;

public class Mach3PendantApplication extends android.app.Application {

    private ConnectivityManager connectivityManager;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public ConnectivityManager getConnectivityManager() {
        return connectivityManager;
    }

    public void setConnectivityManager(ConnectivityManager manager) {
        connectivityManager = manager;
    }

}