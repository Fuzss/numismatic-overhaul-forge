package com.glisco.numismaticoverhaul.mixin;

import com.glisco.numismaticoverhaul.NumismaticOverhaul;
import com.glisco.numismaticoverhaul.item.NumismaticOverhaulItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "dropFromLootTable", at = @At("TAIL"))
    public void injectCoins(DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        if (!this.getType().is(NumismaticOverhaul.THE_BOURGEOISIE)) return;
        if (random.nextFloat() > .5f)
            spawnAtLocation(new ItemStack(NumismaticOverhaulItems.BRONZE_COIN.get(), random.nextIntBetweenInclusive(9, 35)));
        if (random.nextFloat() > .2f) spawnAtLocation(new ItemStack(NumismaticOverhaulItems.SILVER_COIN.get()));
    }

}
