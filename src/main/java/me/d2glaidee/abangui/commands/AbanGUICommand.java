package me.d2glaidee.abangui.commands;

import me.d2glaidee.abangui.AbanGUI;
import me.d2glaidee.abangui.menus.PlayerListMenu;
import me.d2glaidee.abangui.utils.ItemBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AbanGUICommand implements CommandExecutor, TabCompleter {

    private final AbanGUI plugin;

    public AbanGUICommand(AbanGUI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            return handleReload(sender);
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("lang")) {
            return handleLang(sender, args);
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("reasons")) {
            return handleReasons(sender, args);
        }

        var msg = plugin.messages();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ItemBuilder.colorize(msg.prefix() + msg.get("messages.player-only")));
            return true;
        }

        if (!player.hasPermission("abangui.use")) {
            player.sendMessage(ItemBuilder.colorize(msg.prefix() + msg.get("messages.no-permission")));
            return true;
        }

        PlayerListMenu.open(player, 1);
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        // Capture the OLD message manager before reload so the no-permission message
        // is shown in the previously active language.
        var oldMsg = plugin.messages();

        if (!sender.hasPermission("abangui.reload")) {
            sender.sendMessage(ItemBuilder.colorize(oldMsg.prefix() + oldMsg.get("messages.no-permission")));
            return true;
        }

        plugin.reloadAll();

        // After reload, use the NEW manager so the success message is in the new language.
        var msg = plugin.messages();
        sender.sendMessage(ItemBuilder.colorize(msg.prefix() + msg.get("messages.reload-success")));
        sender.sendMessage(ItemBuilder.colorize(
                msg.prefix() + "&7Language: &e" + plugin.config().getLanguage()
                        + " &7(file: &e" + msg.getLoadedFileName() + "&7)"));
        sender.sendMessage(ItemBuilder.colorize(
                msg.prefix() + "&7Reasons loaded: &e" + plugin.reasons().size()));
        return true;
    }

    private boolean handleLang(CommandSender sender, String[] args) {
        var oldMsg = plugin.messages();

        if (!sender.hasPermission("abangui.reload")) {
            sender.sendMessage(ItemBuilder.colorize(oldMsg.prefix() + oldMsg.get("messages.no-permission")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ItemBuilder.colorize(
                    oldMsg.prefix() + "&7Usage: &e/abangui lang <code>"));
            sender.sendMessage(ItemBuilder.colorize(
                    oldMsg.prefix() + "&7Available: &e" + String.join(", ", availableLanguages())));
            return true;
        }

        String requested = args[1].trim().toLowerCase();
        File langFile = new File(plugin.getDataFolder(), "messages_" + requested + ".yml");
        if (!langFile.exists() && plugin.getResource("messages_" + requested + ".yml") == null) {
            sender.sendMessage(ItemBuilder.colorize(
                    oldMsg.prefix() + "&cUnknown language: &e" + requested));
            sender.sendMessage(ItemBuilder.colorize(
                    oldMsg.prefix() + "&7Available: &e" + String.join(", ", availableLanguages())));
            return true;
        }

        plugin.config().setLanguage(requested);
        plugin.reloadAll();

        var msg = plugin.messages();
        sender.sendMessage(ItemBuilder.colorize(
                msg.prefix() + "&aLanguage switched to &e" + requested
                        + " &7(file: &e" + msg.getLoadedFileName() + "&7)"));
        return true;
    }

    private boolean handleReasons(CommandSender sender, String[] args) {
        var msg = plugin.messages();

        if (!sender.hasPermission("abangui.reload")) {
            sender.sendMessage(ItemBuilder.colorize(msg.prefix() + msg.get("messages.no-permission")));
            return true;
        }

        String sub = args.length >= 2 ? args[1].toLowerCase() : "list";

        if (sub.equals("list")) {
            sender.sendMessage(ItemBuilder.colorize(
                    msg.prefix() + "&7Loaded reasons: &e" + plugin.reasons().size()));
            for (var r : plugin.reasons().all()) {
                String time = r.time().isEmpty() ? "&cperm" : "&e" + r.time();
                sender.sendMessage(ItemBuilder.colorize(
                        " &8• &f" + r.key() + " &7→ &e" + r.cmd()
                                + " &7[" + time + "&7] &7" + r.display()));
            }
            return true;
        }

        if (sub.equals("reset")) {
            File diskFile = new File(plugin.getDataFolder(), "reasons.yml");
            if (diskFile.exists()) {
                File backup = new File(plugin.getDataFolder(),
                        "reasons.backup-" + System.currentTimeMillis() + ".yml");
                if (!diskFile.renameTo(backup)) {
                    sender.sendMessage(ItemBuilder.colorize(
                            msg.prefix() + "&cFailed to back up the existing reasons.yml"));
                    return true;
                }
                sender.sendMessage(ItemBuilder.colorize(
                        msg.prefix() + "&7Backed up old file as &e" + backup.getName()));
            }
            plugin.saveResource("reasons.yml", false);
            plugin.reasons().load();
            sender.sendMessage(ItemBuilder.colorize(
                    msg.prefix() + "&aBundled reasons.yml restored — &e"
                            + plugin.reasons().size() + " &aentries loaded."));
            return true;
        }

        sender.sendMessage(ItemBuilder.colorize(
                msg.prefix() + "&7Usage: &e/abangui reasons <list|reset>"));
        return true;
    }

    /** Lists every messages_*.yml present in the plugin data folder. */
    private List<String> availableLanguages() {
        File[] files = plugin.getDataFolder().listFiles((dir, name) ->
                name.startsWith("messages_") && name.endsWith(".yml"));
        if (files == null) return Collections.emptyList();
        List<String> codes = new ArrayList<>();
        for (File f : files) {
            String n = f.getName();
            codes.add(n.substring("messages_".length(), n.length() - ".yml".length()));
        }
        Collections.sort(codes);
        return codes;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> sub = new ArrayList<>();
            if (sender.hasPermission("abangui.reload")) {
                sub.add("reload");
                sub.add("lang");
                sub.add("reasons");
            }
            String input = args[0].toLowerCase();
            List<String> out = new ArrayList<>();
            for (String s : sub) if (s.startsWith(input)) out.add(s);
            return out;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("lang")
                && sender.hasPermission("abangui.reload")) {
            String input = args[1].toLowerCase();
            List<String> out = new ArrayList<>();
            for (String code : availableLanguages()) {
                if (code.startsWith(input)) out.add(code);
            }
            return out;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("reasons")
                && sender.hasPermission("abangui.reload")) {
            String input = args[1].toLowerCase();
            List<String> out = new ArrayList<>();
            for (String s : Arrays.asList("list", "reset")) {
                if (s.startsWith(input)) out.add(s);
            }
            return out;
        }

        return Collections.emptyList();
    }
}
