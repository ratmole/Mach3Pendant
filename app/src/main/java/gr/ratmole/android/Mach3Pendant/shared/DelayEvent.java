package gr.ratmole.android.Mach3Pendant.shared;

public class DelayEvent extends Event {
    private int delayTime;

    public DelayEvent(){

    }

    public DelayEvent(int time) {
        delayTime = time;
    }

    public int getDelayTime() {
        return delayTime;
    }
}
