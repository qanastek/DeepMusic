package com.ceri.deepmusic.ui.yourLibrary.dummy;

import android.util.Log;

import com.ceri.deepmusic.models.IceServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    // Get the IceServer Singleton Instance
    private static IceServer iceServer = IceServer.getInstance();

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Server.Music> ITEMS = new ArrayList<Server.Music>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Server.Music> ITEM_MAP = new HashMap<String, Server.Music>();

    static {

        Server.Music[] items = DummyContent.iceServer.getHello().findAll();

        Log.d("DummyContent", "--------------------");

        // Add some sample items.
        for (Server.Music m: items) {
            Log.d("DummyContent", String.valueOf(m));
            addItem(m);
        }
    }

    private static void addItem(Server.Music m) {
        ITEMS.add(m);
        ITEM_MAP.put(m.identifier < 10 ? "0" + m.identifier : Integer.toString(m.identifier), m);
    }
}