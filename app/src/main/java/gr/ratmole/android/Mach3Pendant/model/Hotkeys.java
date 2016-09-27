package gr.ratmole.android.Mach3Pendant.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import gr.ratmole.android.Mach3Pendant.utils.HotKeysHandler;
import gr.ratmole.android.Mach3Pendant.utils.Log;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Hotkeys {
    private List<Application> apps = new ArrayList<Application>();
    private boolean initialized = false;
    private Application activeApp = null;
    public HashMap<String, Key> oskeys = new HashMap<String, Key>();
    private Context context;
    private static final String PATH_TO_FILE = "/Android/data/gr.ratmole.android.Mach3Pendant";
    private static final String FILE_NAME = "hotkeys.xml";

    public Hotkeys(Context ctx) {
        context = ctx;
        InputStream currentStream = null;
        File hotKeys = new File(getHotkeysFilePath());
        currentStream = getInputStreamFromAssets();
        initHotKeys(currentStream);
    }

    public static String getHotkeysFilePath() {
        return Environment.getExternalStorageDirectory() + PATH_TO_FILE + "/" + FILE_NAME;
    }

    private void initHotKeys(InputStream currentStream) {
        SAXParser parser = initSaxParser();
        HotKeysHandler handler = new HotKeysHandler();
        try {
            parser.parse(currentStream, handler);
            apps = handler.getApps();
            oskeys = handler.getOsKeys();
            initialized = true;
        } catch (FileNotFoundException e) {
            Log.e("File not found", e);
        } catch (SAXException e) {
            Log.e("Incorrect XML file", e);
        } catch (IOException e) {
            Log.e("Can't read file from SD card", e);
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Application getActiveApp() {
        if (!initialized) return null;
        return activeApp;
    }

    /**
     * Set application by new window header
     *
     * @param procName
     * @return true, if active application really has been changed
     */
    public boolean isSupportedApp(String procName) {
        if (!initialized) return false;

        //Search new application
        Application foundApp = null;
        for (Application app : apps) {
            if (procName != null && procName.equalsIgnoreCase(app.procname)) {
                foundApp = app;
            }
        }

        activeApp = foundApp;

        return foundApp != null;
    }


    /**
     * Get InputStream from assets file
     *
     * @return - input stream
     */
    private InputStream getInputStreamFromAssets() {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream input = assetManager.open("hotkeys.xml");
            return input;
        } catch (IOException e) {
            Log.e("IOException", e);
        }
        return null;
    }

    private String pathToFile = null;


    private SAXParser initSaxParser() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            return factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            Log.e("ParserConfigurationException", e);
        } catch (SAXException e) {
            Log.e("SAXException", e);
        }
        return null;
    }
}
