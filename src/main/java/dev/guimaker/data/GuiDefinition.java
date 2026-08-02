package dev.guimaker.data;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;
import java.util.regex.Pattern;

/** A named GUI made of one or more numbered pages. */
public final class GuiDefinition {
    private static final Pattern VALID_ID = Pattern.compile("[a-z0-9_-]{1,32}");

    private final String id;
    private final TreeMap<Integer, PageDefinition> pages = new TreeMap<>();

    public GuiDefinition(String id) {
        this.id = normalizeAndValidateId(id);
    }

    public String id() {
        return id;
    }

    public PageDefinition page(int pageId) {
        return pages.get(pageId);
    }

    public boolean hasPage(int pageId) {
        return pages.containsKey(pageId);
    }

    public Collection<PageDefinition> pages() {
        return Collections.unmodifiableCollection(pages.values());
    }

    public int firstPageId() {
        if (pages.isEmpty()) {
            throw new IllegalStateException("The GUI has no pages.");
        }
        return pages.firstKey();
    }

    public void addPage(PageDefinition page) {
        if (pages.containsKey(page.id())) {
            throw new IllegalArgumentException("This page already exists: " + page.id());
        }
        if (pages.size() >= 64) {
            throw new IllegalArgumentException("A GUI can contain at most 64 pages.");
        }
        pages.put(page.id(), page);
    }

    public void removePage(int pageId) {
        if (!pages.containsKey(pageId)) {
            throw new IllegalArgumentException("Page not found: " + pageId);
        }
        if (pages.size() <= 1) {
            throw new IllegalArgumentException("The GUI's last page cannot be removed.");
        }
        pages.remove(pageId);
    }

    public static String normalizeAndValidateId(String raw) {
        String id = raw == null ? "" : raw.toLowerCase(java.util.Locale.ROOT).strip();
        if (!VALID_ID.matcher(id).matches()) {
            throw new IllegalArgumentException("The GUI ID must be 1-32 characters and may contain only a-z, 0-9, _ and -.");
        }
        return id;
    }
}
