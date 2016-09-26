package gr.ratmole.android.Mach3Pendant.shared;

import java.util.ArrayList;
import java.util.List;

import gr.ratmole.android.Mach3Pendant.utils.Log;

public class EventSequence {
    private List<Event> sequence = new ArrayList<Event>();

    /**
     * Add key press event
     *
     * @param keyCode
     * @return
     */
    public EventSequence press(int keyCode) {
        sequence.add(new KeyEvent(keyCode, true));
        return this;
    }

    /**
     * Add key release event
     *
     * @param keyCode
     * @return
     */
    public EventSequence release(int keyCode) {
        sequence.add(new KeyEvent(keyCode, false));
        return this;
    }


    public EventSequence wheel(int wheelAmt_) {
        sequence.add(new MouseEvent(wheelAmt_));
        return this;
    }

    public List<Event> getSequence() {
        return sequence;
    }

    public Event getSequenceByPossition(int possition) {
        return  sequence.get(possition);

    }
}
