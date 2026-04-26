package me.d2glaidee.abangui;

import me.d2glaidee.abangui.commands.AbanGUICommand;
import me.d2glaidee.abangui.config.ConfigManager;
import me.d2glaidee.abangui.config.MessageManager;
import me.d2glaidee.abangui.config.ReasonManager;
import me.d2glaidee.abangui.listeners.MenuClickListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class AbanGUI extends JavaPlugin {

    private static AbanGUI instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private ReasonManager reasonManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this, configManager.getLanguage());
        reasonManager = new ReasonManager(this);

        var cmd = getCommand("abangui");
        if (cmd != null) {
            var handler = new AbanGUICommand(this);
            cmd.setExecutor(handler);
            cmd.setTabCompleter(handler);
        }

        getServer().getPluginManager().registerEvents(new MenuClickListener(this), this);
        getLogger().info("Loaded — lang=" + configManager.getLanguage());
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static AbanGUI getInstance() {
        return instance;
    }

    public ConfigManager config() {
        return configManager;
    }

    public MessageManager messages() {
        return messageManager;
    }

    public ReasonManager reasons() {
        return reasonManager;
    }

    /**
     * Re-reads config.yml from disk, then rebuilds the message + reason managers.
     * Existing open inventories will keep their old titles; new opens use the new language.
     */
    public void reloadAll() {
        configManager.load();
        messageManager = new MessageManager(this, configManager.getLanguage());
        reasonManager.load();
        getLogger().info("Reloaded — language=" + configManager.getLanguage()
                + ", messages=" + messageManager.getLoadedFileName()
                + ", reasons=" + reasonManager.size());
    }
}
