package com.goswimmy.playerlevels.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class DataManager {

    private static FileConfiguration config;
    private static FileConfiguration messages;
    private static FileConfiguration quests;

    private static File configFile;
    private static File messagesFile;
    private static File questsFile;

    public static void setup(Plugin plugin) {
        if(!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        File folder = plugin.getDataFolder();

        configFile = new File(folder, "config.yml");
        config = plugin.getConfig();
        config.options().copyDefaults(false);
        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            plugin.saveResource("config.yml", true);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        messagesFile = new File(folder, "messages.yml");
        messages = plugin.getConfig();
        messages.options().copyDefaults(false);
        if(!messagesFile.exists()) {
            try {
                messagesFile.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            plugin.saveResource("messages.yml", true);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);

        questsFile = new File(folder, "quests.yml");
        quests = plugin.getConfig();
        quests.options().copyDefaults(false);
        if(!questsFile.exists()) {
            try {
                questsFile.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            plugin.saveResource("quests.yml", true);
        }
        quests = YamlConfiguration.loadConfiguration(questsFile);

        Util.log(Level.INFO, "&a[PlayerLevels] DataManager: loaded!");
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static FileConfiguration getMessages() {
        return messages;
    }

    public static FileConfiguration getQuests() {
        return quests;
    }

    public static void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static void reloadMessages() {
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public static void reloadQuests() {
        quests = YamlConfiguration.loadConfiguration(questsFile);
    }
}
