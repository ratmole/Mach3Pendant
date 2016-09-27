package gr.ratmole.android.Mach3Pendant.model;

import gr.ratmole.android.Mach3Pendant.shared.*;
import gr.ratmole.android.Mach3Pendant.utils.Log;

import java.util.List;
import java.util.StringTokenizer;


public class Key {
    public String id = "";
    public String shortcut = "";
    public int imageResourceId = 0;
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
            int delay = getDelay(token);



            //key code
            if (delay == -1) {
                keyCode = MapStringToKeyCode.map.get(token);

                if (keyCode == null) {
                    Log.e("Can't find key code for symbol '" + token + "'");
                    sequence = null;
                    return null;
                }
               // Log.e("ShortCut: "+ keyCode + " 0x"+Integer.toHexString(keyCode));

                //Skip if only release will be needed
                if (!tokenWithStates.endsWith("$")) {
                    eventList.add(k, new KeyEvent(keyCode, true));
                    k++;
                }
                //Skip if only press was needed
                if (!tokenWithStates.startsWith("$")) {
                    eventList.add(k, new KeyEvent(keyCode, false));
                }
                //Delay
            } else {
                if (!tokenWithStates.endsWith("$")) {
                    eventList.add(k, new DelayEvent(delay));
                    k++;
                }
                if (!tokenWithStates.startsWith("$")) {
                    eventList.add(k, new DelayEvent(delay));
                }

            }
            i++;
        }

        return sequence;
    }

    /**
     * @param input
     * @return -1 if it's not delay, the delay time in milliseconds otherwise
     */
    public int getDelay(String input) {
        try {
            int delay = Integer.parseInt(input);
            //Like a numeric key
            if (delay <= 10) {
                return -1;
            }
            return delay;
        } catch (Exception e) {
            return -1;
        }
    }

}