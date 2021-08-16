package com.goswimmy.playerlevels.managers;

public enum TaskTypes {

    MINE("Mine"),
    AHSELL("Auction Sell"),
    KILL("Kill"),
    ENCHANT("Enchant"),
    GIVE("Give"),
    HARVEST("Harvest"),
    VOTE("Vote");

    private String prettyname;

    TaskTypes(String prettyname) {
        this.prettyname = prettyname;
    }

    public String getPrettyname() {
        return prettyname;
    }
}
