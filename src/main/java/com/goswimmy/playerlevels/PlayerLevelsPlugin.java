package com.goswimmy.playerlevels;

import com.goswimmy.playerlevels.commands.PlayerLevelCommand;
import com.goswimmy.playerlevels.events.*;
import com.goswimmy.playerlevels.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class PlayerLevelsPlugin extends JavaPlugin {

    private static PlayerLevelsPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        //Check for PlaceholderAPI
        if(!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Util.log(Level.WARNING, "&cPlaceholderAPI not found! Plugin will not work as intended!");
        } else {
            new PlaceholderManager().register();
        }

        //Check for NuVotifier
        if(!Bukkit.getPluginManager().isPluginEnabled("Votifier")) {
            Util.log(Level.WARNING, "&cVotifier not found! Plugin will not work as intended!");
        }

        //Check for zAuctionHouse
        if(!Bukkit.getPluginManager().isPluginEnabled("zAuctionHouseV3")) {
            Util.log(Level.WARNING, "&czAuctionHouse not found! Plugin will not work as intended!");
        }

        //Setup DataManager
        DataManager.setup(this);

        //Setup SQLManager
        SQLManager.connect();

        //Register Events
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new JoinEvent(), this);
        pluginManager.registerEvents(new InventoryEvent(), this);
        pluginManager.registerEvents(new BreakEvent(), this);
        pluginManager.registerEvents(new AuctionEvent(), this);
        pluginManager.registerEvents(new EnchantEvent(), this);
        pluginManager.registerEvents(new KillEvent(), this);
        pluginManager.registerEvents(new VoteEvent(), this);
        pluginManager.registerEvents(new CommandPreProcessEvent(), this);

        //Register Commands
        getCommand("quests").setExecutor(new PlayerLevelCommand());
        PlayerLevelCommand.reloadCommands();

        //Register Tab Complete
        getCommand("quests").setTabCompleter(new TabManager());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static PlayerLevelsPlugin getInstance() {
        return instance;
    }
}
