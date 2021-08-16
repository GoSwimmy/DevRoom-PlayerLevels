package com.goswimmy.playerlevels.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderManager extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "playerlevels";
    }

    @Override
    public String getAuthor() {
        return "Sage";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if(player == null) return "";

        if(identifier.equalsIgnoreCase("currentquest")) {
            return PlayerLevelManager.getPrefix(player);
        }

        return null;
    }
}
