package com.glisco.numismaticoverhaul.network;

import com.glisco.numismaticoverhaul.block.ShopBlockEntity;
import com.glisco.numismaticoverhaul.block.ShopOffer;
import com.glisco.numismaticoverhaul.client.gui.ShopScreen;
import com.google.common.collect.Lists;
import fuzs.puzzleslib.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class UpdateShopScreenS2CPacket implements Message<UpdateShopScreenS2CPacket> {
    private List<ShopOffer> offers;
    private long storedCurrency;
    private boolean transferEnabled;

    public UpdateShopScreenS2CPacket() {
    }

    public UpdateShopScreenS2CPacket(List<ShopOffer> offers, long storedCurrency, boolean transferEnabled) {
        this.offers = offers;
        this.storedCurrency = storedCurrency;
        this.transferEnabled = transferEnabled;
    }

    public UpdateShopScreenS2CPacket(ShopBlockEntity shop) {
        this(shop.getOffers(), shop.getStoredCurrency(), shop.isTransferEnabled());
    }

//    public static void handle(UpdateShopScreenS2CPacket message, ClientAccess access) {
//        if (!(access.runtime().currentScreen instanceof ShopScreen screen)) return;
//        screen.update(message);
//    }

//    public static void initialize() {
//        //noinspection ConstantConditions
//        PacketBufSerializer.register(ShopOffer.class, (buf, shopOffer) -> buf.writeNbt(shopOffer.toNbt()), buf -> ShopOffer.fromNbt(buf.readNbt()));
//
//        NumismaticOverhaul.CHANNEL.registerClientbound(UpdateShopScreenS2CPacket.class, UpdateShopScreenS2CPacket::handle);
//    }

    public List<ShopOffer> offers() {
        return offers;
    }

    public long storedCurrency() {
        return storedCurrency;
    }

    public boolean transferEnabled() {
        return transferEnabled;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(offers.size());
        for (ShopOffer offer : offers) {
            buf.writeNbt(offer.toNbt());
        }
        buf.writeLong(storedCurrency);
        buf.writeBoolean(transferEnabled);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.offers = Lists.newArrayListWithCapacity(buf.readVarInt());
        for (int i = 0; i < this.offers.size(); i++) {
            this.offers.add(ShopOffer.fromNbt(buf.readNbt()));
        }
        this.storedCurrency = buf.readLong();
        this.transferEnabled = buf.readBoolean();
    }

    @Override
    public MessageHandler<UpdateShopScreenS2CPacket> makeHandler() {
        return new MessageHandler<UpdateShopScreenS2CPacket>() {
            @Override
            public void handle(UpdateShopScreenS2CPacket message, Player player, Object gameInstance) {
                if (!(((Minecraft) gameInstance).screen instanceof ShopScreen screen)) return;
                screen.update(message);
            }
        };
    }
}
