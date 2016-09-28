package gr.ratmole.android.Mach3Pendant.model;

import gr.ratmole.android.Mach3Pendant.shared.*;
import gr.ratmole.android.Mach3Pendant.utils.Log;

import java.util.List;
import java.util.StringTokenizer;


public class Key {
    public String id = "";
    public String shortcut = "";
    public String label = "";
    private EventSequence sequence = null;
    public Integer keyCode = null;

    public EventSequence getEventSequence() {
        //Lazy create of sequence
        if (sequence != null) return sequence;

        sequence = new EventSequence();
        List<Event> eventList = sequence.getSequence();

        //Parse shortcut string and collect key code sequence
        StringTokenizer st = new StringTokenizer(shortcut, "+$");
        String[] keyCodesWithStates = shortcut.split("\\+");


        int i = 0;
        int k = i;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            String tokenWithStates = keyCodesWithStates[i];

                keyCode = MapStringToKeyCode.map.get(token);

                if (keyCode == null) {
                    Log.e("Can't find key code for symbol '" + token + "'");
                    sequence = null;
                    return null;
                }
                //Skip if only release will be needed
                if (!tokenWithStates.endsWith("$")) {
                    eventList.add(k, new KeyEvent(keyCode, true));
                    k++;
                }
                //Skip if only press was needed
                if (!tokenWithStates.startsWith("$")) {
                    eventList.add(k, new KeyEvent(keyCode, false));
                }
            i++;
        }

        return sequence;
    }

}