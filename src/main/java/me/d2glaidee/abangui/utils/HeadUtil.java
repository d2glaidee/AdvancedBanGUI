package me.d2glaidee.abangui.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class HeadUtil {

    @SuppressWarnings("deprecation")
    public static ItemStack of(String name) {
        return of(Bukkit.getOfflinePlayer(name));
    }

    public static ItemStack of(OfflinePlayer player) {
        var head = new ItemStack(Material.PLAYER_HEAD);
        var skull = (SkullMeta) head.getItemMeta();
        skull.setOwningPlayer(player);
        head.setItemMeta(skull);
        return head;
    }
}
