package gr.ratmole.android.Mach3Pendant.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Application {
    public String name = "";
    public String id = "";
    public List<Key> keys = new ArrayList<Key>();
    public HashMap<String, Key> buttons = new HashMap<String, Key>();
    public String procname = "";
}