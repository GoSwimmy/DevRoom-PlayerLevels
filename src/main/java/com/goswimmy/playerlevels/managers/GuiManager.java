package com.goswimmy.playerlevels.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GuiManager {

    public static HashMap<UUID, Integer> page = new HashMap<>();

    public static void openGui(Player player, int page) {
        GuiManager.page.put(player.getUniqueId(), page);
        FileConfiguration config = DataManager.getConfig();
        FileConfiguration quests = DataManager.getQuests();
        int maxpages = getMaxPages();
        String title = config.getString("gui.title");
        title = title.replace("%page%", String.valueOf(page+1));
        title = title.replace("%max_page%", String.valueOf(maxpages));
        Inventory inventory = Bukkit.createInventory(null, 54, Util.color(title));

        int spot = 10;
        int curquest = SQLManager.getPlayerQuest(player);
        int pageoffset = 0;
        if(page == 0) {
            pageoffset = 1;
            ItemStack def = new ItemStack(Material.valueOf(config.getString("gui.items.default.item").toUpperCase()));
            ItemMeta defm = def.getItemMeta();
            defm.setDisplayName(Util.color(config.getString("gui.items.default.displayname")));
            List<String> lore = new ArrayList<>();
            for(String rawlore : config.getStringList("gui.items.default.lore")) {
                lore.add(Util.color(rawlore));
            }
            defm.setLore(lore);
            def.setItemMeta(defm);
            inventory.setItem(10, def);
            spot++;
        }

        for(int index = (page*(21))+pageoffset; index < ((page+1)*(21+pageoffset))-pageoffset; index++) {
            if(!quests.isSet("quests."+index)) break;
            if(curquest > index) {
                ItemStack itemStack = new ItemStack(Material.valueOf(config.getString("gui.items.completed.item").toUpperCase()));
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(Util.color(config.getString("gui.items.completed.displayname").replace("%quest_id%", String.valueOf(index)).replace("%quest_name%", quests.getString("quests."+index+".name"))));
                List<String> lore = new ArrayList<>();
                for(String rawlore : config.getStringList("gui.items.completed.lore")) {
                    if(rawlore.equalsIgnoreCase("%requirements%")) {
                        for(String task : quests.getStringList("quests."+index+".tasks")) {
                            String[] taskinfo = task.split(":");
                            String amount = taskinfo[1];
                            String message = taskinfo[3];
                            String format = DataManager.getConfig().getString("requirement-completed-format");
                            format = format.replace("%task%", message).replace("%amount%", amount).replace("%max%", amount);
                            lore.add(Util.color(format));
                        }
                        continue;
                    }
                    if(rawlore.equalsIgnoreCase("%rewards%")) {
                        for(String reward : quests.getStringList("quests."+index+".rewards.lore")) {
                            lore.add(Util.color(config.getString("reward-format").replace("%reward%", reward)));
                        }
                        continue;
                    }
                    lore.add(Util.color(rawlore));
                }
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                if(spot == 17) spot = 19;
                if(spot == 26) spot = 28;
                if(spot == 35) spot = 37;
                inventory.setItem(spot, itemStack);
                spot++;
            }
            if(curquest == index) {
                ItemStack itemStack = new ItemStack(Material.valueOf(config.getString("gui.items.in_progress.item").toUpperCase()));
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(Util.color(config.getString("gui.items.in_progress.displayname").replace("%quest_id%", String.valueOf(index)).replace("%quest_name%", quests.getString("quests."+index+".name"))));
                List<String> lore = new ArrayList<>();
                int taskamount = 0;
                int completed = 0;
                ResultSet resultSet = SQLManager.getPlayerData(player);
                List<String> task = quests.getStringList("quests."+index+".tasks");
                for(String rawlore : config.getStringList("gui.items.in_progress.lore")) {
                    if(rawlore.equalsIgnoreCase("%rewards%")) {
                        for(String reward : quests.getStringList("quests."+index+".rewards.lore")) {
                            lore.add(Util.color(config.getString("reward-format").replace("%reward%", reward)));
                        }
                        continue;
                    }
                    if(rawlore.equalsIgnoreCase("%requirements%")) {
                        try {
                            while(resultSet.next()) {
                                int task_index = resultSet.getInt("task_index");
                                int progress = resultSet.getInt("progress");
                                taskamount++;
                                String[] taskinfo = task.get(task_index).split(":");
                                int max = Integer.parseInt(taskinfo[1]);
                                String message = taskinfo[3];
                                if(progress == max) {
                                    completed++;
                                    String format = config.getString("requirement-completed-format");
                                    format = format.replace("%task%", message).replace("%amount%", String.valueOf(progress)).replace("%max%", String.valueOf(max));
                                    lore.add(Util.color(format));
                                } else {
                                    String format = config.getString("requirement-inprogress-format");
                                    format = format.replace("%task%", message).replace("%amount%", String.valueOf(progress)).replace("%max%", String.valueOf(max));
                                    lore.add(Util.color(format));
                                }
                            }
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                        continue;
                    }
                    lore.add(Util.color(rawlore));
                }
                if(completed == taskamount) {
                    for(String rawlore : config.getStringList("gui.items.in_progress.completed-lore")) {
                        lore.add(Util.color(rawlore));
                    }
                } else {
                    for(String rawlore : config.getStringList("gui.items.in_progress.uncompleted-lore")) {
                        lore.add(Util.color(rawlore));
                    }
                }
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                if(spot == 17) spot = 19;
                if(spot == 26) spot = 28;
                if(spot == 35) spot = 37;
                inventory.setItem(spot, itemStack);
                spot++;
            }
            if(curquest < index) {
                ItemStack itemStack = new ItemStack(Material.valueOf(config.getString("gui.items.future.item").toUpperCase()));
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(Util.color(config.getString("gui.items.future.displayname").replace("%quest_index%", String.valueOf(index))));
                List<String> lore = new ArrayList<>();
                for(String rawlore : config.getStringList("gui.items.future.lore")) {
                    lore.add(Util.color(rawlore));
                }
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                if(spot == 17) spot = 19;
                if(spot == 26) spot = 28;
                inventory.setItem(spot, itemStack);
                spot++;
            }
        }
        if(page > 0) {
            ItemStack itemStack = new ItemStack(Material.valueOf(config.getString("gui.items.previous-page.item").toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Util.color(config.getString("gui.items.previous-page.displayname")));
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(37, itemStack);
        }
        if(inventory.getItem(34) != null && (page-1)==maxpages) {
            ItemStack itemStack = new ItemStack(Material.valueOf(config.getString("gui.items.next-page.item").toUpperCase()));
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(Util.color(config.getString("gui.items.next-page.displayname")));
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(43, itemStack);
        }
        player.openInventory(inventory);
    }

    public static int getMaxPages() {
        return (Math.round(DataManager.getQuests().getConfigurationSection("quests").getKeys(false).size()/21)+1);
    }
}
