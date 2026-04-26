package me.d2glaidee.abangui.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private final ItemStack stack;
    private final ItemMeta meta;

    public ItemBuilder(Material mat) {
        stack = new ItemStack(mat);
        meta = stack.getItemMeta();
    }

    public ItemBuilder(ItemStack base) {
        stack = base.clone();
        meta = stack.getItemMeta();
    }

    public ItemBuilder name(String text) {
        meta.displayName(colorize(text));
        return this;
    }

    public ItemBuilder lore(String... lines) {
        meta.lore(Arrays.stream(lines).map(ItemBuilder::colorize).toList());
        return this;
    }

    public ItemBuilder lore(List<String> lines) {
        meta.lore(lines.stream().map(ItemBuilder::colorize).toList());
        return this;
    }

    public ItemBuilder pdc(NamespacedKey key, String value) {
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        return this;
    }

    public ItemBuilder pdc(NamespacedKey key, int value) {
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
        return this;
    }

    public ItemBuilder hideFlags() {
        meta.addItemFlags(ItemFlag.values());
        return this;
    }

    public ItemStack build() {
        stack.setItemMeta(meta);
        return stack;
    }

    public static Component colorize(String text) {
        return LEGACY.deserialize(text);
    }
}
