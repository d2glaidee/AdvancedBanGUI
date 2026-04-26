package me.d2glaidee.abangui.menus;

import me.d2glaidee.abangui.AbanGUI;
import me.d2glaidee.abangui.config.ReasonManager;
import me.d2glaidee.abangui.utils.ItemBuilder;
import me.d2glaidee.abangui.utils.PDCKeys;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ReasonMenu {

    private static final String MARKER = "abangui";

    public static void open(Player viewer, String targetName, ActionType action) {
        var plugin = AbanGUI.getInstance();
        var msg = plugin.messages();
        var cfg = plugin.config();

        List<ReasonManager.ReasonEntry> reasons = plugin.reasons().forCommand(action.id);

        if (reasons.isEmpty()) {
            String text = msg.prefix() + msg.get("messages.no-reasons");
            viewer.sendMessage(ItemBuilder.colorize(text));
            return;
        }

        String title = msg.get("menu.reason-menu-title",
                "{action}", action.id.toUpperCase(),
                "{player}", targetName);

        int rows = cfg.getReasonMenuRows();
        int size = rows * 9;
        Inventory inv = Bukkit.createInventory(null, size, ItemBuilder.colorize(title));

        int slot = 0;
        int maxSlot = (rows - 1) * 9;
        for (var reason : reasons) {
            if (slot >= maxSlot) break;

            String timeDisplay = reason.time().isEmpty() ? "&cPermanent" : "&e" + reason.time();

            inv.setItem(slot, new ItemBuilder(Material.PAPER)
                    .name(reason.display())
                    .lore("&7Command: &f/" + reason.cmd(), "&7Duration: " + timeDisplay)
                    .pdc(PDCKeys.reasonKey(), reason.key())
                    .pdc(PDCKeys.targetPlayer(), targetName)
                    .pdc(PDCKeys.actionType(), action.id)
                    .pdc(PDCKeys.guiMarker(), MARKER)
                    .build());

            slot++;
        }

        int navStart = (rows - 1) * 9;
        for (int i = navStart; i < size; i++) {
            inv.setItem(i, filler());
        }

        inv.setItem(navStart, new ItemBuilder(Material.ARROW)
                .name(msg.get("buttons.back"))
                .pdc(PDCKeys.actionType(), "back_to_actions")
                .pdc(PDCKeys.targetPlayer(), targetName)
                .pdc(PDCKeys.guiMarker(), MARKER)
                .build());

        inv.setItem(navStart + 4, new ItemBuilder(Material.BARRIER)
                .name(msg.get("buttons.close"))
                .pdc(PDCKeys.actionType(), "close")
                .pdc(PDCKeys.guiMarker(), MARKER)
                .build());

        viewer.openInventory(inv);
    }

    private static ItemStack filler() {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name(" ")
                .pdc(PDCKeys.guiMarker(), MARKER)
                .build();
    }
}
