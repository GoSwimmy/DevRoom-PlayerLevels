package com.goswimmy.playerlevels.events;

import com.goswimmy.playerlevels.managers.DataManager;
import com.goswimmy.playerlevels.managers.GuiManager;
import com.goswimmy.playerlevels.managers.PlayerLevelManager;
import com.goswimmy.playerlevels.managers.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryEvent implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        String name = event.getView().getTitle();
        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String nametest = ChatColor.stripColor(Util.color(DataManager.getConfig().getString("gui.title").split(" ")[0]));
        if(ChatColor.stripColor(name.toLowerCase()).contains(nametest.toLowerCase())) {
            if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                FileConfiguration config = DataManager.getConfig();
                ItemStack item = event.getCurrentItem();
                int page = GuiManager.page.get(player.getUniqueId());
                if(item.getType() == Material.valueOf(config.getString("gui.items.future.item").toUpperCase())) {
                    player.sendMessage(Util.color(Util.getMessage("locked-quest")));
                }
                if(item.getType() == Material.valueOf(config.getString("gui.items.completed.item").toUpperCase()) && event.getSlot() != 10) {
                    player.sendMessage(Util.color(Util.getMessage("quest-already-completed")));
                }
                if(item.getType() == Material.valueOf(config.getString("gui.items.in_progress.item").toUpperCase())) {
                    boolean complete = false;
                    String test = ChatColor.stripColor(Util.color(DataManager.getConfig().getStringList("gui.items.in_progress.completed-lore").get(0)));
                    for(String lore : item.getItemMeta().getLore()) {
                        if (lore.contains(test)) {
                            complete = true;
                            break;
                        }
                    }
                    if(complete) {
                        PlayerLevelManager.checkComplete(player, true);
                    } else {
                        PlayerLevelManager.checkGive(player);
                        PlayerLevelManager.sendTaskUncomplete(player);
                    }
                }
                if(ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase()).contains(ChatColor.stripColor(Util.color(config.getString("gui.items.next-page.displayname"))))) {
                    GuiManager.openGui(player, page+1);
                }
                if(ChatColor.stripColor(item.getItemMeta().getDisplayName().toLowerCase()).contains(ChatColor.stripColor(Util.color(config.getString("gui.items.previous-page.displayname"))))) {
                    GuiManager.openGui(player, page-1);
                }
            }
            event.setCancelled(true);
        }
    }
}
