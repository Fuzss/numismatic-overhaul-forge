package com.glisco.numismaticoverhaul;

import net.minecraftforge.common.ForgeConfigSpec;

public class NumismaticOverhaulConfigModel {
    public static final NumismaticOverhaulConfigModel INSTANCE = new NumismaticOverhaulConfigModel();
    private final ForgeConfigSpec spec;

    public ForgeConfigSpec.BooleanValue enableVillagerTrading;

    public ForgeConfigSpec.BooleanValue generateCurrencyInChests;

    public ForgeConfigSpec.IntValue pursePositionX;

    public ForgeConfigSpec.IntValue pursePositionY;

    public NumismaticOverhaulConfigModel.LootOptions lootOptions;

    private NumismaticOverhaulConfigModel() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        this.enableVillagerTrading = builder.comment("Whether villagers should use Numismatic currency for trading").define("enableVillagerTrading", true);
        this.generateCurrencyInChests = builder.comment("Whether Numismatic currency should be injected ForgeConfigSpec.IntValueo the loot tables of loot chests").define("generateCurrencyInChests", true);
        this.pursePositionX = builder.comment("Where the purse in your inventory should be placed on the X axis").defineInRange("pursePositionX", 129, 0, Integer.MAX_VALUE);
        this.pursePositionY = builder.comment("Where the purse in your inventory should be placed on the Y axis").defineInRange("pursePositionY", 20, 0, Integer.MAX_VALUE);
        this.lootOptions = new LootOptions(builder);
        this.spec = builder.build();
    }

    public ForgeConfigSpec getSpec() {
        return this.spec;
    }

    public static class LootOptions {
        public ForgeConfigSpec.IntValue desertMinLoot;
        public ForgeConfigSpec.IntValue desertMaxLoot;
        public ForgeConfigSpec.IntValue dungeonMinLoot;
        public ForgeConfigSpec.IntValue dungeonMaxLoot;

        public ForgeConfigSpec.IntValue structureMinLoot;
        public ForgeConfigSpec.IntValue structureMaxLoot;

        public ForgeConfigSpec.IntValue strongholdLibraryMinLoot;
        public ForgeConfigSpec.IntValue strongholdLibraryMaxLoot;

        public LootOptions(ForgeConfigSpec.Builder builder) {
            builder.push("loot");
            this.desertMinLoot = builder.comment("Affects money gained from Dungeon and Mineshaft chests").defineInRange("desertMinLoot", 300, 0, Integer.MAX_VALUE);
            this.desertMaxLoot = builder.comment("Affects money gained from Dungeon and Mineshaft chests").defineInRange("desertMaxLoot", 1200, 0, Integer.MAX_VALUE);
            this.dungeonMinLoot = builder.comment("Affects money gained from Dungeon and Mineshaft chests").defineInRange("dungeonMinLoot", 500, 0, Integer.MAX_VALUE);
            this.dungeonMaxLoot = builder.comment("Affects money gained from Dungeon and Mineshaft chests").defineInRange("dungeonMaxLoot", 2000, 0, Integer.MAX_VALUE);
            this.structureMinLoot = builder.comment("Affects money gained from Bastion, Stronghold, Outpost and Buried Treasure chests").defineInRange("structureMinLoot", 1500, 0, Integer.MAX_VALUE);
            this.structureMaxLoot = builder.comment("Affects money gained from Bastion, Stronghold, Outpost and Buried Treasure chests").defineInRange("structureMaxLoot", 4000, 0, Integer.MAX_VALUE);
            this.strongholdLibraryMinLoot = builder.defineInRange("strongholdLibraryMinLoot", 2000, 0, Integer.MAX_VALUE);
            this.strongholdLibraryMaxLoot = builder.defineInRange("strongholdLibraryMaxLoot", 6000, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }
}
