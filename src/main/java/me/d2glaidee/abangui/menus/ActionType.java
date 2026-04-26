package me.d2glaidee.abangui.menus;

import org.bukkit.Material;

public enum ActionType {

    // --- Punishments (need a reason) ---
    BAN       ("ban",       Material.RED_CONCRETE,         true),
    TEMPBAN   ("tempban",   Material.ORANGE_CONCRETE,      true),
    BANIP     ("banip",     Material.REDSTONE_BLOCK,       true),
    TEMPIPBAN ("tempipban", Material.NETHER_BRICKS,        true),
    MUTE      ("mute",      Material.YELLOW_CONCRETE,      true),
    TEMPMUTE  ("tempmute",  Material.GOLD_BLOCK,           true),
    KICK      ("kick",      Material.PISTON,               true),

    WARN      ("warn",      Material.BLUE_CONCRETE,        true),
    TEMPWARN  ("tempwarn",  Material.LIGHT_BLUE_CONCRETE,  true),
    NOTE      ("note",      Material.WRITABLE_BOOK,        true),

    // --- Removals (no reason) ---
    UNBAN     ("unban",     Material.LIME_CONCRETE,        false),
    UNBANIP   ("unbanip",   Material.SLIME_BLOCK,          false),
    UNMUTE    ("unmute",    Material.GREEN_CONCRETE,       false),
    UNWARN    ("unwarn",    Material.CYAN_CONCRETE,        false),
    UNNOTE    ("unnote",    Material.LIGHT_GRAY_CONCRETE,  false),

    // --- Info / read-only (no reason) ---
    CHECK     ("check",     Material.COMPASS,              false),
    HISTORY   ("history",   Material.BOOK,                 false),
    WARNS     ("warns",     Material.WRITTEN_BOOK,         false),
    NOTES     ("notes",     Material.ENCHANTED_BOOK,       false),
    BANLIST   ("banlist",   Material.KNOWLEDGE_BOOK,       false);

    public final String id;
    public final Material material;
    public final boolean hasReasons;

    ActionType(String id, Material material, boolean hasReasons) {
        this.id = id;
        this.material = material;
        this.hasReasons = hasReasons;
    }

    public String permission() {
        return "abangui." + id;
    }

    public static ActionType fromId(String id) {
        for (var type : values()) {
            if (type.id.equalsIgnoreCase(id)) return type;
        }
        return null;
    }
}
