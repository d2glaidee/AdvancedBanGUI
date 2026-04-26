package me.d2glaidee.abangui.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MessageManager {

    private final YamlConfiguration yaml;
    private final String loadedFileName;

    public MessageManager(JavaPlugin plugin, String language) {
        extractAllLanguages(plugin);

        String fileName = "messages_" + language + ".yml";
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            if (plugin.getResource(fileName) != null) {
                plugin.saveResource(fileName, false);
            } else {
                plugin.getLogger().warning(fileName + " not found — falling back to messages_en.yml");
                fileName = "messages_en.yml";
                file = new File(plugin.getDataFolder(), fileName);
                if (!file.exists()) plugin.saveResource(fileName, false);
            }
        }

        this.loadedFileName = fileName;
        yaml = YamlConfiguration.loadConfiguration(file);

        // Always layer the bundled jar version on top as defaults so newly added
        // keys (e.g. player-head.*) work even if a server owner kept an old file.
        var bundled = plugin.getResource(fileName);
        if (bundled != null) {
            yaml.setDefaults(YamlConfiguration.loadConfiguration(
                    new InputStreamReader(bundled, StandardCharsets.UTF_8)));
        }

        plugin.getLogger().info("Messages loaded from " + file.getAbsolutePath());
    }

    /**
     * Extracts every bundled messages_*.yml from the plugin jar into the data folder
     * so server owners can edit any localization without having to switch the language first.
     * Existing files on disk are never overwritten.
     */
    private void extractAllLanguages(JavaPlugin plugin) {
        try {
            URL jarUrl = plugin.getClass().getProtectionDomain().getCodeSource().getLocation();
            File jarFile = new File(jarUrl.toURI());
            if (!jarFile.isFile()) return; // running from IDE / not a real jar

            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (entry.isDirectory()) continue;
                    if (!name.startsWith("messages_") || !name.endsWith(".yml")) continue;
                    if (name.contains("/")) continue;

                    File target = new File(plugin.getDataFolder(), name);
                    if (!target.exists()) {
                        plugin.saveResource(name, false);
                    }
                }
            }
        } catch (IOException | java.net.URISyntaxException ex) {
            plugin.getLogger().warning("Could not enumerate bundled language files: " + ex.getMessage());
        }
    }

    public String getLoadedFileName() {
        return loadedFileName;
    }

    public String get(String path) {
        return yaml.getString(path, "&cMissing: " + path);
    }

    public String get(String path, String... pairs) {
        String result = get(path);
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            result = result.replace(pairs[i], pairs[i + 1]);
        }
        return result;
    }

    public List<String> getList(String path) {
        var list = yaml.getStringList(path);
        return list.isEmpty() ? Collections.singletonList("&7...") : list;
    }

    public String prefix() {
        return get("prefix");
    }
}
