package com.ceri.deepmusic.ui.musics.dummy;

import com.ceri.deepmusic.models.Music;

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

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Music> ITEMS = new ArrayList<Music>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Music> ITEM_MAP = new HashMap<String, Music>();

    private static final int COUNT = 2500;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(i);
        }
    }

    private static void addItem(int i) {
        Music m = createDummyItem(i);
        ITEMS.add(m);
        ITEM_MAP.put(i < 10 ? "0" + i : Integer.toString(i), m);
    }

    private static Music createDummyItem(int position) {
        return new Music("Music " + position, "Mr. Kitty", "2:52");
    }
}