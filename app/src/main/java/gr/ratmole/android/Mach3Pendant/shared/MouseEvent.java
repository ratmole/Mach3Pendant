package gr.ratmole.android.Mach3Pendant.shared;

public class MouseEvent extends Event {
    public enum Type {
        WHEEL,
        BUTTON
    }

    private int wheelAmt;
    private Type eventType;
    private int button;//1,2,3
    private boolean press;

    public MouseEvent(int wheelAmt_) {
        eventType = Type.WHEEL;
        wheelAmt = wheelAmt_;
    }

    public MouseEvent(int button_, boolean press_) {
        eventType = Type.BUTTON;
        button = button_;
        press = press_;
    }

    public MouseEvent() {

    }

    public int getWheelAmt() {
        return wheelAmt;
    }

    public Type getEventType() {
        return eventType;
    }

    public int getButton() {
        return button;
    }

    public boolean isPress() {
        return press;
    }
}
