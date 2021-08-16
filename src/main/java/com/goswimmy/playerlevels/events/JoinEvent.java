package com.goswimmy.playerlevels.events;

import com.goswimmy.playerlevels.managers.SQLManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!SQLManager.doesPlayerExist(player)) {
            SQLManager.setupPlayer(player, 1);
        }
    }
}
