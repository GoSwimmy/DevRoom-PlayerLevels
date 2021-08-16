package com.goswimmy.playerlevels.managers;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private static final Pattern hexPattern = Pattern.compile("(?<!\\\\)(#[a-fA-F0-9]{6})");

    public static String color(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        Matcher matcher = hexPattern.matcher(message);
        while (matcher.find()) {
            String color = matcher.group();
            message = message.replace(color, String.valueOf(ChatColor.of(color)));
        }
        return message;
    }

    public static void log(Level level, String message) {
        Bukkit.getLogger().log(level, color(message));
    }

    public static String getPrefix() {
        FileConfiguration messages = DataManager.getMessages();
        return messages.getString("messages.prefix") + " ";
    }

    public static String getMessage(String node) {
        FileConfiguration messages = DataManager.getMessages();
        return messages.getString("messages."+node);
    }

    public static String getPermission(String node) {
        FileConfiguration config = DataManager.getConfig();
        return config.getString("permissions."+node);
    }
}
