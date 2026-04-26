package me.d2glaidee.abangui.menus;

import me.d2glaidee.abangui.AbanGUI;
import me.d2glaidee.abangui.utils.HeadUtil;
import me.d2glaidee.abangui.utils.ItemBuilder;
import me.d2glaidee.abangui.utils.PDCKeys;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlayerListMenu {

    private static final String MARKER = "abangui";

    public static void open(Player viewer, int page) {
        var plugin = AbanGUI.getInstance();
        var cfg = plugin.config();
        var msg = plugin.messages();

        List<OfflinePlayer> players = gatherPlayers();
        int perPage = cfg.getPlayersPerPage();
        int maxPage = Math.max(1, (int) Math.ceil((double) players.size() / perPage));
        int current = Math.max(1, Math.min(page, maxPage));

        String title = msg.get("menu.player-list-title",
                "{page}", String.valueOf(current),
                "{max_page}", String.valueOf(maxPage));

        int rows = cfg.getPlayerListRows();
        int size = rows * 9;
        Inventory inv = Bukkit.createInventory(null, size, ItemBuilder.colorize(title));

        int from = (current - 1) * perPage;
        int to = Math.min(from + perPage, players.size());

        for (int i = from; i < to; i++) {
            OfflinePlayer target = players.get(i);
            String name = target.getName() != null ? target.getName() : "???";
            String status = target.isOnline()
                    ? msg.get("player-head.online")
                    : msg.get("player-head.offline");

            ItemStack head = new ItemBuilder(HeadUtil.of(target))
                    .name("&e" + name)
                    .lore(status, msg.get("player-head.click-hint"))
                    .pdc(PDCKeys.targetPlayer(), name)
                    .pdc(PDCKeys.guiMarker(), MARKER)
                    .build();

            inv.setItem(i - from, head);
        }

        int navStart = (rows - 1) * 9;
        for (int i = navStart; i < size; i++) {
            inv.setItem(i, filler());
        }

        if (current > 1) {
            inv.setItem(navStart, new ItemBuilder(Material.ARROW)
                    .name(msg.get("buttons.prev-page"))
                    .pdc(PDCKeys.pageNumber(), current - 1)
                    .pdc(PDCKeys.guiMarker(), MARKER)
                    .build());
        }

        inv.setItem(navStart + 4, new ItemBuilder(Material.BARRIER)
                .name(msg.get("buttons.close"))
                .pdc(PDCKeys.actionType(), "close")
                .pdc(PDCKeys.guiMarker(), MARKER)
                .build());

        if (current < maxPage) {
            inv.setItem(navStart + 8, new ItemBuilder(Material.ARROW)
                    .name(msg.get("buttons.next-page"))
                    .pdc(PDCKeys.pageNumber(), current + 1)
                    .pdc(PDCKeys.guiMarker(), MARKER)
                    .build());
        }

        viewer.openInventory(inv);
    }

    private static List<OfflinePlayer> gatherPlayers() {
        Comparator<OfflinePlayer> byName = Comparator.comparing(
                p -> p.getName() != null ? p.getName().toLowerCase() : "");

        var online = new ArrayList<OfflinePlayer>();
        for (Player p : Bukkit.getOnlinePlayers()) online.add(p);
        online.sort(byName);

        var offline = new ArrayList<OfflinePlayer>();
        for (OfflinePlayer op : Bukkit.getOfflinePlayers()) {
            if (op.getName() == null || op.isOnline()) continue;
            offline.add(op);
        }
        offline.sort(byName);

        var result = new ArrayList<OfflinePlayer>(online.size() + offline.size());
        result.addAll(online);
        result.addAll(offline);
        return result;
    }

    private static ItemStack filler() {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .name(" ")
                .pdc(PDCKeys.guiMarker(), MARKER)
                .build();
    }
}
