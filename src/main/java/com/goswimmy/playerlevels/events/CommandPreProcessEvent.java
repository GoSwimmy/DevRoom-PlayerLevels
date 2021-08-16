package com.goswimmy.playerlevels.events;

import com.goswimmy.playerlevels.commands.PlayerLevelCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;

public class CommandPreProcessEvent implements Listener {

    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String[] args = event.getMessage().split(" ");
        String command = args[0].replace("/", "");
        if(PlayerLevelCommand.getCommands().contains(command)) {
            args = Arrays.copyOfRange(args, 1, args.length);
            PlayerLevelCommand.runCommand(player, args);
            event.setCancelled(true);
        }
    }
}
