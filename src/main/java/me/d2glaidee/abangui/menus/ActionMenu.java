package me.d2glaidee.abangui.menus;

import me.d2glaidee.abangui.AbanGUI;
import me.d2glaidee.abangui.utils.ItemBuilder;
import me.d2glaidee.abangui.utils.PDCKeys;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ActionMenu {

    private static final String MARKER = "abangui";

    // Slot layout for a 5-row inventory (45 slots).
    // Indices match ActionType.values() order.
    //
    // Row 0 (punishments):  BAN  TEMPBAN  BANIP  TEMPIPBAN  .  MUTE  TEMPMUTE  .  KICK
    // Row 1 (warns/notes):  WARN TEMPWARN .     NOTE
    // Row 2 (removals):     .    UNBAN UNBANIP UNMUTE UNWARN UNNOTE
    // Row 3 (info):         .    CHECK    HISTORY WARNS  NOTES   .   BANLIST
    // Row 4 (nav):          BACK . . . CLOSE
    private static final int[] SLOTS = {
            0, 1, 2, 3, 5, 6, 8,        // BAN, TEMPBAN, BANIP, TEMPIPBAN, MUTE, TEMPMUTE, KICK
            9, 10, 12,                  // WARN, TEMPWARN, NOTE
            19, 20, 21, 22, 23,         // UNBAN, UNBANIP, UNMUTE, UNWARN, UNNOTE
            28, 29, 30, 31, 33          // CHECK, HISTORY, WARNS, NOTES, BANLIST
    };

    public static void open(Player viewer, String targetName) {
        var msg = AbanGUI.getInstance().messages();
        int rows = AbanGUI.getInstance().config().getActionMenuRows();
        int size = rows * 9;

        String title = msg.get("menu.action-menu-title", "{player}", targetName);
        Inventory inv = Bukkit.createInventory(null, size, ItemBuilder.colorize(title));

        ActionType[] actions = ActionType.values();
        for (int i = 0; i < actions.length && i < SLOTS.length; i++) {
            int slot = SLOTS[i];
            if (slot >= size - 9) continue; // never overlap the nav row

            ActionType action = actions[i];
            inv.setItem(slot, new ItemBuilder(action.material)
                    .name(msg.get("buttons." + action.id))
                    .lore(msg.getList("buttons." + action.id + "-lore"))
                    .pdc(PDCKeys.actionType(), action.id)
                    .pdc(PDCKeys.targetPlayer(), targetName)
                    .pdc(PDCKeys.guiMarker(), MARKER)
                    .build());
        }

        int navStart = (rows - 1) * 9;

        inv.setItem(navStart, new ItemBuilder(Material.ARROW)
                .name(msg.get("buttons.back"))
                .pdc(PDCKeys.actionType(), "back_to_list")
                .pdc(PDCKeys.guiMarker(), MARKER)
                .build());

        inv.setItem(navStart + 4, new ItemBuilder(Material.BARRIER)
                .name(msg.get("buttons.close"))
                .pdc(PDCKeys.actionType(), "close")
                .pdc(PDCKeys.guiMarker(), MARKER)
                .build());

        viewer.openInventory(inv);
    }
}
