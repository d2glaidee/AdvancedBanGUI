package me.d2glaidee.abangui.utils;

import me.d2glaidee.abangui.AbanGUI;
import org.bukkit.NamespacedKey;

import java.util.concurrent.ConcurrentHashMap;

public final class PDCKeys {

    private static final ConcurrentHashMap<String, NamespacedKey> cache = new ConcurrentHashMap<>();

    public static NamespacedKey targetPlayer() { return resolve("target_player"); }
    public static NamespacedKey actionType()   { return resolve("action_type"); }
    public static NamespacedKey reasonKey()    { return resolve("reason_key"); }
    public static NamespacedKey pageNumber()   { return resolve("page_number"); }
    public static NamespacedKey guiMarker()    { return resolve("gui_marker"); }

    private static NamespacedKey resolve(String id) {
        return cache.computeIfAbsent(id, k -> new NamespacedKey(AbanGUI.getInstance(), k));
    }
}
