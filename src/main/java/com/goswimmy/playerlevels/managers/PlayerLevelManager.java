package com.goswimmy.playerlevels.managers;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerLevelManager {

    public static void checkBlockBreak(Player player, Material block) {
        FileConfiguration quests = DataManager.getQuests();
        int quest = SQLManager.getPlayerQuest(player);
        @NotNull List<String> tasks = quests.getStringList("quests."+quest+".tasks");
        for(int index = 0; index < tasks.size(); index++) {
            String[] taskinfo = tasks.get(index).split(":");
            String type = taskinfo[0];
            if(type.equalsIgnoreCase("mine")) {
                String item = taskinfo[2];
                int max = Integer.parseInt(taskinfo[1]);
                if(block.name().equals(item.toUpperCase())) {
                    SQLManager.addProgress(player, index, max);
                }
            }
        }
    }

    public static void checkComplete(Player player, boolean claim) {
        FileConfiguration quests = DataManager.getQuests();

        int curquest = SQLManager.getPlayerQuest(player);
        boolean finished = true;

        List<String> tasks = quests.getStringList("quests."+curquest+".tasks");

        for(int index = 0; index < tasks.size(); index++) {
            int progress = SQLManager.getTaskProgress(player, index);
            String[] taskinfo = tasks.get(index).split(":");
            int max = Integer.parseInt(taskinfo[1]);
            if(progress < max) {
                finished = false;
            } else {
                if(SQLManager.getTaskStatus(player, index) == 0) {
                    String type = taskinfo[0];
                    type = TaskTypes.valueOf(type.toUpperCase()).getPrettyname();
                    String object = WordUtils.capitalizeFully(taskinfo[2].replace("_", " "));
                    PlayerLevelManager.sendTaskComplete(player, type, max, object);
                    SQLManager.setTaskStatus(player, index, 1);
                }
            }
        }

        if(finished && claim) {
            SQLManager.setupPlayer(player, curquest+1);
            giveRewards(player, curquest);
            player.sendMessage(Util.color(Util.getPrefix() + Util.getMessage("new-quest").replace("%quest_id%", String.valueOf(curquest+1))));
            GuiManager.openGui(player, GuiManager.page.get(player.getUniqueId()));
        }
    }

    public static void giveRewards(Player player, int quest) {
        FileConfiguration quests = DataManager.getQuests();
        for(String command : quests.getStringList("quests."+quest+".rewards.commands")) {
            command = command.replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public static void checkKill(Player player, Entity entity) {
        FileConfiguration quests = DataManager.getQuests();
        int quest = SQLManager.getPlayerQuest(player);
        @NotNull List<String> tasks = quests.getStringList("quests."+quest+".tasks");
        for(int index = 0; index < tasks.size(); index++) {
            String[] taskinfo = tasks.get(index).split(":");
            String type = taskinfo[0];
            String object = taskinfo[2].toUpperCase();
            if(type.equalsIgnoreCase("kill") && entity.getType().equals(EntityType.valueOf(object))) {
                int max = Integer.parseInt(taskinfo[1]);
                SQLManager.addProgress(player, index, max);
            }
        }
    }

    public static void checkEnchant(Player player) {
        FileConfiguration quests = DataManager.getQuests();
        int quest = SQLManager.getPlayerQuest(player);
        @NotNull List<String> tasks = quests.getStringList("quests."+quest+".tasks");
        for(int index = 0; index < tasks.size(); index++) {
            String[] taskinfo = tasks.get(index).split(":");
            String type = taskinfo[0];
            if(type.equalsIgnoreCase("enchant")) {
                int max = Integer.parseInt(taskinfo[1]);
                SQLManager.addProgress(player, index, max);
            }
        }
    }

    public static void checkAuctionSell(Player player) {
        FileConfiguration quests = DataManager.getQuests();
        int quest = SQLManager.getPlayerQuest(player);
        @NotNull List<String> tasks = quests.getStringList("quests."+quest+".tasks");
        for(int index = 0; index < tasks.size(); index++) {
            String[] taskinfo = tasks.get(index).split(":");
            String type = taskinfo[0];
            if(type.equalsIgnoreCase("ahsell")) {
                int max = Integer.parseInt(taskinfo[1]);
                SQLManager.addProgress(player, index, max);
            }
        }
    }

    public static void checkVote(Player player) {
        FileConfiguration quests = DataManager.getQuests();
        int quest = SQLManager.getPlayerQuest(player);
        @NotNull List<String> tasks = quests.getStringList("quests."+quest+".tasks");
        for(int index = 0; index < tasks.size(); index++) {
            String[] taskinfo = tasks.get(index).split(":");
            String type = taskinfo[0];
            if(type.equalsIgnoreCase("vote")) {
                int max = Integer.parseInt(taskinfo[1]);
                SQLManager.addProgress(player, index, max);
            }
        }
    }

    public static void checkGive(Player player) {
        FileConfiguration quests = DataManager.getQuests();
        int quest = SQLManager.getPlayerQuest(player);
        @NotNull List<String> tasks = quests.getStringList("quests."+quest+".tasks");
        for(int index = 0; index < tasks.size(); index++) {
            String[] taskinfo = tasks.get(index).split(":");
            String type = taskinfo[0];
            if(type.equalsIgnoreCase("give")) {
                int max = Integer.parseInt(taskinfo[1]);
                if(SQLManager.getTaskProgress(player, index) == max) continue;
                int itemamount = 0;
                Material object = Material.valueOf(taskinfo[2].toUpperCase());
                for(ItemStack itemStack : player.getInventory().getContents()) {
                    if(itemStack == null) continue;
                    if(itemStack.getType().equals(object)) {
                        itemamount += itemStack.getAmount();
                    }
                }
                if(itemamount >= max) {
                    int tempitem = max;
                    for(ItemStack itemStack : player.getInventory().getContents()) {
                        if(itemStack == null) continue;
                        if(itemStack.getType().equals(object)) {
                            if(tempitem > 64) {
                                itemStack.setAmount(0);
                                tempitem -= 64;
                            } else {
                                itemStack.setAmount(itemStack.getAmount()-tempitem);
                                tempitem -= itemStack.getAmount();
                            }
                        }
                    }
                    SQLManager.addGiveProgress(player, index, max);
                }
            }
        }
        GuiManager.openGui(player, 0);
    }

    public static void sendTaskComplete(Player player, String type, int max, String object) {
        String format = Util.getMessage("task-completed");
        format = format.replace("%type%", type);
        format = format.replace("%amount%", String.valueOf(max));
        format = format.replace("%object%", object);
        player.sendMessage(Util.color(format));
    }

    public static String getPrefix(Player player) {
        int quest = SQLManager.getPlayerQuest(player);
        if(quest > 0) quest--;
        return Util.color(DataManager.getQuests().getString("quests."+quest+".prefix").replace("%quest_id%", String.valueOf(quest)));
    }

    public static void sendTaskUncomplete(Player player) {
        player.sendMessage(Util.color(Util.getMessage("task-uncomplete")));
    }
}
