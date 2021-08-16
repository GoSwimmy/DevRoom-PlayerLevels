package com.goswimmy.playerlevels.events;

import com.goswimmy.playerlevels.managers.PlayerLevelManager;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionTransactionEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AuctionEvent implements Listener {

    @EventHandler
    public void onAuctionSell(AuctionTransactionEvent event) {
        Player player = Bukkit.getPlayer(event.getTransaction().getSeller());
        PlayerLevelManager.checkAuctionSell(player);
    }
}
