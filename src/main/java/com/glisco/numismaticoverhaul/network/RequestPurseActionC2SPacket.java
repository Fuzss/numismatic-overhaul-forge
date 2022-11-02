package com.glisco.numismaticoverhaul.network;

import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.currency.CurrencyConverter;
import com.glisco.numismaticoverhaul.currency.CurrencyHelper;
import fuzs.puzzleslib.network.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;

public class RequestPurseActionC2SPacket implements Message<RequestPurseActionC2SPacket> {
    private Action action;
    private long value;

    public RequestPurseActionC2SPacket() {
    }

    public RequestPurseActionC2SPacket(Action action, long value) {
        this.action = action;
        this.value = value;
    }

//    public static void handle(RequestPurseActionC2SPacket message, ServerAccess access) {
//        var player = access.player();
//        long value = message.value();
//
//        if (player.currentScreenHandler instanceof PlayerScreenHandler) {
//            switch (message.action()) {
//                case STORE_ALL -> ModComponents.CURRENCY.get(player).modify(CurrencyHelper.getMoneyInInventory(player, true));
//                case EXTRACT -> {
//                    //Check if we can actually extract this much money to prevent cheeky packet forgery
//                    if (ModComponents.CURRENCY.get(player).getValue() < value) return;
//
//                    CurrencyConverter.getAsItemStackList(value).forEach(stack -> player.getInventory().offerOrDrop(stack));
//                    ModComponents.CURRENCY.get(player).modify(-value);
//                }
//                case EXTRACT_ALL -> {
//                    CurrencyConverter.getAsValidStacks(ModComponents.CURRENCY.get(player).getValue()).forEach(stack -> player.getInventory().offerOrDrop(stack));
//
//                    ModComponents.CURRENCY.get(player).modify(-ModComponents.CURRENCY.get(player).getValue());
//                }
//            }
//        }
//    }

//    private static boolean isInventorioHandler(ServerPlayerEntity player) {
//        return FabricLoader.getInstance().isModLoaded("inventorio")
//                && player.currentScreenHandler.getClass().getName().equals("me.lizardofoz.inventorio.player.InventorioScreenHandler");
//    }

    public static RequestPurseActionC2SPacket storeAll() {
        return new RequestPurseActionC2SPacket(Action.STORE_ALL, 0);
    }

    public static RequestPurseActionC2SPacket extractAll() {
        return new RequestPurseActionC2SPacket(Action.EXTRACT_ALL, 0);
    }

    public static RequestPurseActionC2SPacket extract(long amount) {
        return new RequestPurseActionC2SPacket(Action.EXTRACT, amount);
    }

    public Action action() {
        return action;
    }

    public long value() {
        return value;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeEnum(action);
        buf.writeLong(value);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        action = buf.readEnum(Action.class);
        value = buf.readLong();
    }

    @Override
    public MessageHandler<RequestPurseActionC2SPacket> makeHandler() {
        return new MessageHandler<RequestPurseActionC2SPacket>() {
            @Override
            public void handle(RequestPurseActionC2SPacket message, Player player, Object gameInstance) {
                long value = message.value();

                if (player.containerMenu instanceof InventoryMenu) {
                    switch (message.action()) {
                        case STORE_ALL -> ModComponents.CURRENCY.get(player).modify(CurrencyHelper.getMoneyInInventory(player, true));
                        case EXTRACT -> {
                            //Check if we can actually extract this much money to prevent cheeky packet forgery
                            if (ModComponents.CURRENCY.get(player).getValue() < value) return;

                            CurrencyConverter.getAsItemStackList(value).forEach(stack -> player.getInventory().placeItemBackInInventory(stack));
                            ModComponents.CURRENCY.get(player).modify(-value);
                        }
                        case EXTRACT_ALL -> {
                            CurrencyConverter.getAsValidStacks(ModComponents.CURRENCY.get(player).getValue()).forEach(stack -> player.getInventory().placeItemBackInInventory(stack));

                            ModComponents.CURRENCY.get(player).modify(-ModComponents.CURRENCY.get(player).getValue());
                        }
                    }
                }
            }
        };
    }


    public enum Action {
        STORE_ALL, EXTRACT, EXTRACT_ALL
    }
}
