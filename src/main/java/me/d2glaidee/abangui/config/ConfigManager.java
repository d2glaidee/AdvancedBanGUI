package me.d2glaidee.abangui.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ConfigManager {

    private final JavaPlugin plugin;

    private String language;
    private int playersPerPage;
    private int playerListRows;
    private int actionMenuRows;
    private int reasonMenuRows;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        load();
    }

    /**
     * Re-reads config.yml directly from disk, bypassing any Bukkit-side cache.
     * Used both on startup and on every /abangui reload.
     */
    public void load() {
        File file = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        language = cfg.getString("language", "en").trim().toLowerCase();
        playersPerPage = cfg.getInt("players-per-page", 45);
        playerListRows = cfg.getInt("gui.player-list-rows", 6);
        actionMenuRows = cfg.getInt("gui.action-menu-rows", 4);
        reasonMenuRows = cfg.getInt("gui.reason-menu-rows", 6);

        plugin.getLogger().info("Config loaded from " + file.getAbsolutePath()
                + " — language=" + language);
    }

    /**
     * Persists a new language code to config.yml on disk and reloads everything.
     */
    public void setLanguage(String newLanguage) {
        File file = new File(plugin.getDataFolder(), "config.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        cfg.set("language", newLanguage.trim().toLowerCase());
        try {
            cfg.save(file);
        } catch (Exception ex) {
            plugin.getLogger().warning("Failed to save config.yml: " + ex.getMessage());
        }
        load();
    }

    public String getLanguage()     { return language; }
    public int getPlayersPerPage()  { return playersPerPage; }
    public int getPlayerListRows()  { return playerListRows; }
    public int getActionMenuRows()  { return actionMenuRows; }
    public int getReasonMenuRows()  { return reasonMenuRows; }
}
