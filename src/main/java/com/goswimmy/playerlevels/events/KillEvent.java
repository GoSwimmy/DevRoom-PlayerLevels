package com.goswimmy.playerlevels.events;

import com.goswimmy.playerlevels.managers.PlayerLevelManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillEvent implements Listener {

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player killer = event.getEntity().getKiller();
        if(killer != null) {
            Player player = event.getEntity().getKiller();
            PlayerLevelManager.checkKill(player, entity);
        }
    }
}
