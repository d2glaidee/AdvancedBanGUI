package me.d2glaidee.abangui.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReasonManager {

    private final JavaPlugin plugin;
    private final Map<String, ReasonEntry> entries = new LinkedHashMap<>();

    public ReasonManager(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    /**
     * Re-reads reasons.yml directly from disk. Always clears the previous state
     * before populating so removed reasons actually disappear.
     */
    public void load() {
        entries.clear();

        File file = new File(plugin.getDataFolder(), "reasons.yml");
        if (!file.exists()) plugin.saveResource("reasons.yml", false);

        var cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = cfg.getConfigurationSection("reasons");
        if (section == null) {
            plugin.getLogger().warning("reasons.yml has no top-level 'reasons:' section — no reasons loaded");
            return;
        }

        for (String key : section.getKeys(false)) {
            var block = section.getConfigurationSection(key);
            if (block == null) continue;
            entries.put(key, new ReasonEntry(
                    key,
                    block.getString("display", key),
                    block.getString("time", ""),
                    block.getString("cmd", "ban")
            ));
        }

        plugin.getLogger().info("Reasons loaded from " + file.getAbsolutePath()
                + " — " + entries.size() + " entries");
    }

    public ReasonEntry get(String key) {
        return entries.get(key);
    }

    public List<ReasonEntry> forCommand(String cmd) {
        return entries.values().stream()
                .filter(r -> r.cmd().equalsIgnoreCase(cmd))
                .toList();
    }

    public Collection<ReasonEntry> all() {
        return Collections.unmodifiableCollection(entries.values());
    }

    public int size() {
        return entries.size();
    }

    public static String buildCommand(ReasonEntry reason, String targetPlayer) {
        String cleanDisplay = reason.display().replaceAll("&[0-9a-fk-orA-FK-OR]", "");
        boolean temporal = reason.cmd().startsWith("temp");

        if (temporal && !reason.time().isEmpty()) {
            return reason.cmd() + " " + targetPlayer + " " + reason.time() + " " + cleanDisplay;
        }
        return reason.cmd() + " " + targetPlayer + " " + cleanDisplay;
    }

    public record ReasonEntry(String key, String display, String time, String cmd) {}
}
