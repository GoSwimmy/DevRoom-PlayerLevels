package com.goswimmy.playerlevels.commands;

import com.goswimmy.playerlevels.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.logging.Level;

public class PlayerLevelCommand implements CommandExecutor {

    private static ArrayList<String> commands = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            runCommand(player, args);
        }
        return false;
    }

    public static void runCommand(Player player, String[] args) {
        if(args.length == 3) {
            if(args[0].equalsIgnoreCase("setlevel")) {
                if(player.hasPermission(Util.getPermission("reload"))) {
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                    try {
                        int level = Integer.parseInt(args[2]);
                        SQLManager.setupPlayer(target, level);
                        player.sendMessage(Util.color("&aQuest Level set to " + level+"!"));
                    } catch (IllegalArgumentException exception) {
                        player.sendMessage(Util.color("&cInvalid usage, try /<command> setlevel <player> <level>"));
                    }
                } else {
                    player.sendMessage(Util.color(Util.getPrefix() + DataManager.getMessages().getString("messages.no-permission")));
                }
                return;
            }
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (player.hasPermission(Util.getPermission("reload"))) {
                    DataManager.reloadMessages();
                    DataManager.reloadConfig();
                    DataManager.reloadQuests();
                    reloadCommands();
                    player.sendMessage(Util.color(Util.getPrefix() + DataManager.getMessages().getString("messages.reload")));
                } else {
                    player.sendMessage(Util.color(Util.getPrefix() + DataManager.getMessages().getString("messages.no-permission")));
                }
                return;
            }
        }
        GuiManager.openGui(player, 0);
    }

    public static ArrayList<String> getCommands() {
        return commands;
    }

    public static void reloadCommands() {
        FileConfiguration config = DataManager.getConfig();
        commands.clear();
        commands.addAll(config.getStringList("commands"));
        Util.log(Level.INFO, "&a[PlayerLevels] CommandManager: loaded "+commands.size()+" commands!");
    }
}
