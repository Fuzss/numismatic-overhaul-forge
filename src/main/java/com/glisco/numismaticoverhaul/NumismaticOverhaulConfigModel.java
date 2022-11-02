package com.glisco.numismaticoverhaul;

import fuzs.puzzleslib.config.ConfigCore;
import fuzs.puzzleslib.config.annotation.Config;

public class NumismaticOverhaulConfigModel implements ConfigCore {

    @Config(description = "Whether villagers should use Numismatic currency for trading")
    public boolean enableVillagerTrading = true;

    @Config(description = "Whether taxes from Minecraft Comes Alive: Reborn should be delivered as Numismatic currency")
    public boolean enableMcaCompatibility = true;

    @Config(description = "Whether Numismatic currency should be injected into the loot tables of loot chests")
    public boolean generateCurrencyInChests = true;

    @Config(description = "Where the purse in your inventory should be placed on the X axis")
    public int pursePositionX = 129;

    @Config(description = "Where the purse in your inventory should be placed on the Y axis")
    public int pursePositionY = 20;

    @Config
    public LootOptions_ lootOptions = new LootOptions_();

    public static class LootOptions_ implements ConfigCore {
        @Config(description = "Affects money gained from Dungeon and Mineshaft chests")
        public int desertMinLoot = 300;
        @Config(description = "Affects money gained from Dungeon and Mineshaft chests")
        public int desertMaxLoot = 1200;
        @Config(description = "Affects money gained from Dungeon and Mineshaft chests")
        public int dungeonMinLoot = 500;
        @Config(description = "Affects money gained from Dungeon and Mineshaft chests")
        public int dungeonMaxLoot = 2000;

        @Config(description = "Affects money gained from Bastion, Stronghold, Outpost and Buried Treasure chests")
        public int structureMinLoot = 1500;
        public int structureMaxLoot = 4000;

        @Config
        public int strongholdLibraryMinLoot = 2000;
        @Config
        public int strongholdLibraryMaxLoot = 6000;
    }
}
