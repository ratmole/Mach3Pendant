package gr.ratmole.android.Mach3Pendant.shared;

/**
 * Created by IntelliJ IDEA.
 * User: pilgr
 * Date: 10.07.11
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
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
