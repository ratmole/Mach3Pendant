package gr.ratmole.android.Mach3Pendant.shared;

public class ServerWindow {
    private String title = "";
    private String processName = "";

    public ServerWindow() {

    }

    public ServerWindow(String title_, String processName_) {
        title = title_;
        processName = processName_;
    }

    public String getTitle() {
        return title;
    }

    public String getProcessName() {
        return processName;
    }
}
