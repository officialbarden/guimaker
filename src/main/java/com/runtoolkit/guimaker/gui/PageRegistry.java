package com.runtoolkit.guimaker.gui;

import java.util.HashMap;
import java.util.Map;

/**
 * PageRegistry — Fixed, in-mod registry of page data.
 *
 * IN THE ORIGINAL DATAPACK, pages were stored in an NBT storage called
 * "gm:storage", as free-form data writable by the player through a dialog
 * (GUI[{GUI_ID}].PAGES[{PAGE}].PAGE_CONTENTS). Here, every page is
 * PREDEFINED via mod code or a JSON config on the server side; a player
 * cannot inject new "executable" content at runtime.
 */
public final class PageRegistry {

    private static final Map<Integer, PageData> PAGES = new HashMap<>();

    private PageRegistry() {}

    public static void register(int pageId, PageData data) {
        PAGES.put(pageId, data);
    }

    public static PageData get(int pageId) {
        return PAGES.get(pageId);
    }

    public static boolean exists(int pageId) {
        return PAGES.containsKey(pageId);
    }

    /**
     * PageData — the fixed definition of a page: title and row count.
     * Slot contents (which button is bound to which ButtonAction) also
     * only ever use the ButtonAction enum + a fixed parameter; no field
     * ever carries a free command/function string.
     */
    public record PageData(String title, int rows) {}
}
