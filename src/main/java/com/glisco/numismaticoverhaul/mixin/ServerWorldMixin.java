package com.glisco.numismaticoverhaul.mixin;

import com.glisco.numismaticoverhaul.villagers.json.VillagerTradesHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

@Mixin(ServerLevel.class)
public class ServerWorldMixin {

    @Inject(method = "addNewPlayer", at = @At("TAIL"))
    public void playerConnect(ServerPlayer player, CallbackInfo ci) {
        VillagerTradesHandler.broadcastErrors(Collections.singletonList(player));
    }

}
