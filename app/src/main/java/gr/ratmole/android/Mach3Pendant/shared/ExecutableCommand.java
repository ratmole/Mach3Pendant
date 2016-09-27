package gr.ratmole.android.Mach3Pendant.shared;


public class ExecutableCommand extends Event {
    private String command;

    public ExecutableCommand() {
    }

    public ExecutableCommand(String command_) {
        this.command = command_;
    }

    public String getCommand() {
        return command;
    }
}
