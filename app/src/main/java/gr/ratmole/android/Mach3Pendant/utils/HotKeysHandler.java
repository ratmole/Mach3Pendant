package gr.ratmole.android.Mach3Pendant.utils;

import gr.ratmole.android.Mach3Pendant.model.Application;
import gr.ratmole.android.Mach3Pendant.model.Key;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HotKeysHandler extends DefaultHandler {

    private List<Application> apps = new ArrayList<Application>();
    private HashMap<String, Key> osKeys = new HashMap<String, Key>();
    private Application app = null;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("application")) {
            // If current tag correspond to active mobile operator
            app = new Application();
            app.id = attributes.getValue("id");
            app.name = attributes.getValue("name");
            app.procname = attributes.getValue("procname");

            if (app.name == null) {
                Log.e("[hotkeys.xml] Tag 'name' not specified for app id " + app.id);
                app.name = "";
            }
            if (app.procname == null) {
                Log.e("[hotkeys.xml] Tag 'procname' not specified for app id " + app.id);
                app.procname = "";
            }
        } else if (qName.equalsIgnoreCase("hotkey")) {
            Key key = new Key();
            key.id = attributes.getValue("id");
            key.shortcut = attributes.getValue("shortcut");
            key.label = attributes.getValue("label");
            // Mach3PendantApplication shortcut
            if (app != null) {
                app.keys.add(key);
            } else {
                osKeys.put(key.id, key);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("application")) {
            apps.add(app);
            app = null;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
    }

    public List<Application> getApps() {
        return apps;
    }

    public HashMap<String, Key> getOsKeys() {
        return osKeys;
    }

}
