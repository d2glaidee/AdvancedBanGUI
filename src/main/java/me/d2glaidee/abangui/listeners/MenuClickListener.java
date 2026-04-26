package me.d2glaidee.abangui.listeners;

import me.d2glaidee.abangui.AbanGUI;
import me.d2glaidee.abangui.config.ReasonManager;
import me.d2glaidee.abangui.menus.ActionMenu;
import me.d2glaidee.abangui.menus.ActionType;
import me.d2glaidee.abangui.menus.PlayerListMenu;
import me.d2glaidee.abangui.menus.ReasonMenu;
import me.d2glaidee.abangui.utils.ItemBuilder;
import me.d2glaidee.abangui.utils.PDCKeys;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MenuClickListener implements Listener {

    private final AbanGUI plugin;

    public MenuClickListener(AbanGUI plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        PersistentDataContainer pdc = clicked.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(PDCKeys.guiMarker(), PersistentDataType.STRING)) return;

        event.setCancelled(true);

        var msg = plugin.messages();
        String actionId = pdc.getOrDefault(PDCKeys.actionType(), PersistentDataType.STRING, "");
        String targetName = pdc.getOrDefault(PDCKeys.targetPlayer(), PersistentDataType.STRING, "");
        String reasonKey = pdc.getOrDefault(PDCKeys.reasonKey(), PersistentDataType.STRING, "");

        // Close button
        if (actionId.equals("close")) {
            player.closeInventory();
            return;
        }

        // Back to player list from action menu
        if (actionId.equals("back_to_list")) {
            PlayerListMenu.open(player, 1);
            return;
        }

        // Back to action menu from reason menu
        if (actionId.equals("back_to_actions")) {
            ActionMenu.open(player, targetName);
            return;
        }

        // Pagination arrows
        if (pdc.has(PDCKeys.pageNumber(), PersistentDataType.INTEGER)) {
            int page = pdc.getOrDefault(PDCKeys.pageNumber(), PersistentDataType.INTEGER, 1);
            PlayerListMenu.open(player, page);
            return;
        }

        // Player head clicked — open action menu for that player
        if (!targetName.isEmpty() && actionId.isEmpty() && reasonKey.isEmpty()) {
            ActionMenu.open(player, targetName);
            return;
        }

        // Reason item clicked — build and dispatch the punishment command
        if (!reasonKey.isEmpty() && !targetName.isEmpty()) {
            ReasonManager.ReasonEntry reason = plugin.reasons().get(reasonKey);
            if (reason == null) return;

            if (!player.hasPermission("abangui." + reason.cmd())) {
                player.sendMessage(ItemBuilder.colorize(
                        msg.prefix() + msg.get("messages.action-no-permission",
                                "{action}", reason.cmd())));
                return;
            }

            String fullCommand = ReasonManager.buildCommand(reason, targetName);
            player.closeInventory();

            // Run as console so the punishment goes through regardless of the staff member's AB perms
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), fullCommand);

            player.sendMessage(ItemBuilder.colorize(
                    msg.prefix() + msg.get("messages.action-executed",
                            "{action}", reason.cmd(),
                            "{player}", targetName)));
            return;
        }

        // Action button clicked in the action menu
        if (!actionId.isEmpty() && !targetName.isEmpty()) {
            ActionType action = ActionType.fromId(actionId);
            if (action == null) return;

            // Permission check
            if (!player.hasPermission(action.permission())) {
                player.sendMessage(ItemBuilder.colorize(
                        msg.prefix() + msg.get("messages.action-no-permission",
                                "{action}", action.id)));
                return;
            }

            // Actions that need a reason submenu
            if (action.hasReasons) {
                ReasonMenu.open(player, targetName, action);
                return;
            }

            // Direct actions — no reason needed
            handleDirectAction(player, action, targetName);
        }
    }

    private void handleDirectAction(Player player, ActionType action, String targetName) {
        var msg = plugin.messages();

        switch (action) {
            // --- Removals (run as console so they always go through) ---

            case UNBAN -> {
                // /unban [PLAYER]
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "unban " + targetName);
                player.closeInventory();
                player.sendMessage(ItemBuilder.colorize(
                        msg.prefix() + msg.get("messages.action-executed",
                                "{action}", "unban", "{player}", targetName)));
            }
            case UNBANIP -> {
                // /unbanip [PLAYER/IP] — lifts an IP ban (resolves IP via player name)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "unbanip " + targetName);
                player.closeInventory();
                player.sendMessage(ItemBuilder.colorize(
                        msg.prefix() + msg.get("messages.action-executed",
                                "{action}", "unbanip", "{player}", targetName)));
            }
            case UNMUTE -> {
                // /unmute [PLAYER]
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "unmute " + targetName);
                player.closeInventory();
                player.sendMessage(ItemBuilder.colorize(
                        msg.prefix() + msg.get("messages.action-executed",
                                "{action}", "unmute", "{player}", targetName)));
            }
            case UNWARN -> {
                // /unwarn clear [PLAYER] — removes all warnings for the player
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "unwarn clear " + targetName);
                player.closeInventory();
                player.sendMessage(ItemBuilder.colorize(
                        msg.prefix() + msg.get("messages.action-executed",
                                "{action}", "unwarn", "{player}", targetName)));
            }
            case UNNOTE -> {
                // /unnote clear [PLAYER] — removes all notes for the player
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "unnote clear " + targetName);
                player.closeInventory();
                player.sendMessage(ItemBuilder.colorize(
                        msg.prefix() + msg.get("messages.action-executed",
                                "{action}", "unnote", "{player}", targetName)));
            }

            // --- Read-only / info (run as the player so they see the chat output) ---

            case CHECK -> {
                // /check [PLAYER] — UUID/IP/Country/Ban-Status/Mute-Status/Warn-Count/Note-Count
                player.closeInventory();
                Bukkit.dispatchCommand(player, "check " + targetName);
            }
            case HISTORY -> {
                // /history [PLAYER] — full punishment log (paged)
                player.closeInventory();
                Bukkit.dispatchCommand(player, "history " + targetName);
            }
            case WARNS -> {
                // /warns [PLAYER] — list of warnings
                player.closeInventory();
                Bukkit.dispatchCommand(player, "warns " + targetName);
            }
            case NOTES -> {
                // /notes [PLAYER] — list of notes
                player.closeInventory();
                Bukkit.dispatchCommand(player, "notes " + targetName);
            }
            case BANLIST -> {
                // /banlist <PAGE> — global list of currently active punishments
                player.closeInventory();
                Bukkit.dispatchCommand(player, "banlist 1");
            }

            default -> {}
        }
    }

    // Block drag events inside our GUI inventories
    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        var topInventory = event.getView().getTopInventory();
        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot >= topInventory.getSize()) continue;

            for (var item : topInventory.getContents()) {
                if (item != null && item.hasItemMeta()
                        && item.getItemMeta().getPersistentDataContainer()
                        .has(PDCKeys.guiMarker(), PersistentDataType.STRING)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
