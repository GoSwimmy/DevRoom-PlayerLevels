package com.goswimmy.playerlevels.events;

import com.goswimmy.playerlevels.managers.PlayerLevelManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakEvent implements Listener {

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material block = event.getBlock().getType();
        PlayerLevelManager.checkBlockBreak(player, block);
    }
}
