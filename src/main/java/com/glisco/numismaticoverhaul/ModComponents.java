package com.glisco.numismaticoverhaul;

import com.glisco.numismaticoverhaul.currency.CurrencyComponent;
import fuzs.puzzleslib.capability.CapabilityController;
import fuzs.puzzleslib.capability.data.PlayerCapabilityKey;
import fuzs.puzzleslib.capability.data.PlayerRespawnStrategy;
import fuzs.puzzleslib.capability.data.SyncStrategy;
import fuzs.puzzleslib.core.CommonFactories;
import net.minecraft.world.entity.player.Player;

public class ModComponents {
    private static final CapabilityController CAPABILITY = CommonFactories.INSTANCE.capabilities(NumismaticOverhaul.MOD_ID);

    public static final PlayerCapabilityKey<CurrencyComponent> CURRENCY = CAPABILITY.registerPlayerCapability("currency", CurrencyComponent.class, (Object provider) -> new CurrencyComponent((Player) provider), PlayerRespawnStrategy.ALWAYS_COPY, SyncStrategy.SELF);

    public static void touch() {

    }
}
