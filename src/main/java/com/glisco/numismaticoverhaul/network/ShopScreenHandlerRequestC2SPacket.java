package com.glisco.numismaticoverhaul.network;

import com.glisco.numismaticoverhaul.block.ShopScreenHandler;
import fuzs.puzzleslib.network.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class ShopScreenHandlerRequestC2SPacket implements Message<ShopScreenHandlerRequestC2SPacket> {
    private Action action;
    private long value;

    public ShopScreenHandlerRequestC2SPacket() {
    }

    public ShopScreenHandlerRequestC2SPacket(Action action, long value) {
        this.action = action;
        this.value = value;
    }

    public ShopScreenHandlerRequestC2SPacket(Action action) {
        this(action, 0);
    }

//    public static void handle(ShopScreenHandlerRequestC2SPacket message, ServerAccess access) {
//        final var player = access.player();
//        final long value = message.value();
//
//        if (!(player.currentScreenHandler instanceof ShopScreenHandler shopHandler)) return;
//
//        switch (message.action()) {
//            case LOAD_OFFER -> shopHandler.loadOffer(value);
//            case CREATE_OFFER -> shopHandler.createOffer(value);
//            case DELETE_OFFER -> shopHandler.deleteOffer();
//            case EXTRACT_CURRENCY -> shopHandler.extractCurrency();
//            case TOGGLE_TRANSFER -> shopHandler.toggleTransfer();
//        }
//    }

    public Action action() {
        return action;
    }

    public long value() {
        return value;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeLong(this.value);
        buf.writeEnum(this.action);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        this.value = buf.readLong();
        this.action = buf.readEnum(Action.class);
    }

    @Override
    public MessageHandler<ShopScreenHandlerRequestC2SPacket> makeHandler() {
        return new MessageHandler<ShopScreenHandlerRequestC2SPacket>() {
            @Override
            public void handle(ShopScreenHandlerRequestC2SPacket message, Player player, Object gameInstance) {
                final long value = message.value();

                if (!(player.containerMenu instanceof ShopScreenHandler shopHandler)) return;

                switch (message.action()) {
                    case LOAD_OFFER -> shopHandler.loadOffer(value);
                    case CREATE_OFFER -> shopHandler.createOffer(value);
                    case DELETE_OFFER -> shopHandler.deleteOffer();
                    case EXTRACT_CURRENCY -> shopHandler.extractCurrency();
                    case TOGGLE_TRANSFER -> shopHandler.toggleTransfer();
                }
            }
        };
    }


    public enum Action {
        CREATE_OFFER, DELETE_OFFER, LOAD_OFFER, EXTRACT_CURRENCY, TOGGLE_TRANSFER
    }

}
