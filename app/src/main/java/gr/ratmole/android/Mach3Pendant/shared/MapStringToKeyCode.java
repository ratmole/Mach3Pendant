package gr.ratmole.android.Mach3Pendant.shared;

import java.util.HashMap;

import static gr.ratmole.android.Mach3Pendant.shared.KeyEvent.*;

/**
 * Created by IntelliJ IDEA.
 * User: pilgr
 * Date: 09.06.11
 * Time: 18:16
 * To change this template use File | Settings | File Templates.
 */
public final class MapStringToKeyCode {
    public static final HashMap<String, Integer> map = new HashMap<String, Integer>();

    static {
        put("Ctrl", VK_CONTROL);
        put("Shift", VK_SHIFT);
        put("Alt", VK_ALT);
        put("Tab", VK_TAB);
        put("Win", VK_WINDOWS);
        put("Esc", VK_ESCAPE);

        put("F1", VK_F1);
        put("F2", VK_F2);
        put("F3", VK_F3);
        put("F4", VK_F4);
        put("F5", VK_F5);
        put("F6", VK_F6);
        put("F7", VK_F7);
        put("F8", VK_F8);
        put("F9", VK_F9);
        put("F10", VK_F10);
        put("F11", VK_F11);
        put("F12", VK_F12);

        put("Q", VK_Q);
        put("W", VK_W);
        put("E", VK_E);
        put("R", VK_R);
        put("T", VK_T);
        put("Y", VK_Y);
        put("U", VK_U);
        put("I", VK_I);
        put("O", VK_O);
        put("P", VK_P);
        put("A", VK_A);
        put("S", VK_S);
        put("D", VK_D);
        put("F", VK_F);
        put("G", VK_G);
        put("H", VK_H);
        put("J", VK_J);
        put("K", VK_K);
        put("L", VK_L);
        put("Z", VK_Z);
        put("X", VK_X);
        put("C", VK_C);
        put("V", VK_V);
        put("B", VK_B);
        put("N", VK_N);
        put("M", VK_M);

        put("1", VK_1);
        put("2", VK_2);
        put("3", VK_3);
        put("4", VK_4);
        put("5", VK_5);
        put("6", VK_6);
        put("7", VK_7);
        put("8", VK_8);
        put("9", VK_9);
        put("0", VK_0);

        put("Del", VK_DELETE);
        put("Ins", VK_INSERT);
        put("PgUp", VK_PAGE_UP);
        put("PgDn", VK_PAGE_DOWN);
        put("Home", VK_HOME);
        put("End", VK_END);
        put("Enter", VK_ENTER);
        put("Space", VK_SPACE);
        put("Left", VK_LEFT);
        put("Right", VK_RIGHT);
        put("Up", VK_UP);
        put("Down", VK_DOWN);
        put("Pause", VK_PAUSE);
        put("Backspace", VK_BACK_SPACE);
        put("Backquote", VK_BACK_QUOTE);
        put("Forwardslash", VK_SLASH);
        put("NumPadPlus", VK_ADD);
        put("NumPadMinus", VK_SUBTRACT);



    }

    private static void put(String key, int value) {
        map.put(key, value);
    }
}
