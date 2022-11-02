package com.glisco.numismaticoverhaul.mixin;

import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.NumismaticOverhaul;
import com.glisco.numismaticoverhaul.currency.CurrencyConverter;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "destroyVanishingCursedItems", at = @At("TAIL"))
    public void onServerDeath(CallbackInfo ci) {
        var player = (Player) (Object) this;

        final var world = player.level;
        if (world.isClientSide) return;

        final var component = ModComponents.CURRENCY.get(player);

        // additional clamp as Forge doesn't support bounded int game rules
        var dropPercentage = Mth.clamp(world.getGameRules().getRule(NumismaticOverhaul.MONEY_DROP_PERCENTAGE).get(), 0, 100) * .01f;
        int dropped = (int) (component.getValue() * dropPercentage);

        var stacksDropped = CurrencyConverter.getAsValidStacks(dropped);
        for (var drop : stacksDropped) {
            for (int i = 0; i < drop.getCount(); i++) {
                player.drop(numismaticoverhaul$singleCopy(drop), true, false);
            }
        }

        component.modify(-dropped);
    }

    @Unique
    private static ItemStack numismaticoverhaul$singleCopy(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.setCount(1);
        return copy;
    }
}
