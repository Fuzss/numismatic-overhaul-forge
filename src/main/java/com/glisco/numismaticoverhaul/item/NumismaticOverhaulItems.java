package com.glisco.numismaticoverhaul.item;

import com.glisco.numismaticoverhaul.NumismaticOverhaul;
import com.glisco.numismaticoverhaul.currency.Currency;
import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.init.RegistryManager;
import fuzs.puzzleslib.init.RegistryReference;
import net.minecraft.world.item.Item;

public class NumismaticOverhaulItems {
    private static final RegistryManager REGISTRY = CommonFactories.INSTANCE.registration(NumismaticOverhaul.MOD_ID);
    public static final RegistryReference<Item> BRONZE_COIN = REGISTRY.registerItem("bronze_coin", () -> new CoinItem(Currency.BRONZE));
    public static final RegistryReference<Item> SILVER_COIN = REGISTRY.registerItem("silver_coin", () -> new CoinItem(Currency.SILVER));
    public static final RegistryReference<Item> GOLD_COIN = REGISTRY.registerItem("gold_coin", () -> new CoinItem(Currency.GOLD));
    public static final RegistryReference<Item> MONEY_BAG = REGISTRY.registerItem("money_bag", () -> new MoneyBagItem());

    public static void touch() {}
}
