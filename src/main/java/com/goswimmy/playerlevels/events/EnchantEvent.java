package com.goswimmy.playerlevels.events;

import com.goswimmy.playerlevels.managers.PlayerLevelManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantEvent implements Listener {

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        Player player = event.getEnchanter();
        PlayerLevelManager.checkEnchant(player);
    }

}
