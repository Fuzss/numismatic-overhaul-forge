package com.glisco.numismaticoverhaul.block;

import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.NumismaticOverhaul;
import com.glisco.numismaticoverhaul.client.gui.ShopScreen;
import com.glisco.numismaticoverhaul.network.ShopScreenHandlerRequestC2SPacket;
import com.glisco.numismaticoverhaul.network.UpdateShopScreenS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShopScreenHandler extends AbstractContainerMenu {

    private final Player owner;

    private final Container shopInventory;
    private final SimpleContainer bufferInventory = new SimpleContainer(1);

    private final List<ShopOffer> offers;

    private ShopBlockEntity shop = null;

    public ShopScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(27));
    }

    public ShopScreenHandler(int syncId, Inventory playerInventory, Container shopInventory) {
        super(NumismaticOverhaul.SHOP_SCREEN_HANDLER_TYPE.get(), syncId);
        this.shopInventory = shopInventory;
        this.owner = playerInventory.player;

        if (!this.owner.level.isClientSide) {
            this.shop = (ShopBlockEntity) shopInventory;
            this.offers = shop.getOffers();
        } else {
            this.offers = new ArrayList<>();
        }

//        SlotGenerator.begin(this::addSlot, 8, 17)
//                .slotFactory((inv, index, x, y) -> new AutoHidingSlot(inv, index, x, y, 0, false))
//                .grid(this.shopInventory, 0, 9, 3)
//                .slotFactory(Slot::new)
//                .moveTo(8, 85)
//                .playerInventory(playerInventory);

        //Trade Buffer Slot
        this.bufferInventory.addListener(this::onBufferChanged);
        this.addSlot(new AutoHidingSlot(bufferInventory, 0, 186, 14, 0, true) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                ItemStack shadow = stack.copy();
                this.set(shadow);
                return false;
            }

            @Override
            public boolean mayPickup(Player playerEntity) {
                this.set(ItemStack.EMPTY);
                return false;
            }
        });
    }

    private void onBufferChanged(Container inventory) {
        if (this.owner.level.isClientSide && Minecraft.getInstance().screen instanceof ShopScreen screen) {
            screen.afterDataUpdate();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return this.shopInventory.stillValid(player);
    }

    public void loadOffer(long index) {
        if (!this.owner.level.isClientSide) {
            if (index > this.offers.size() - 1) {
                NumismaticOverhaul.LOGGER.error("Player {} attempted to load invalid trade at index {}", owner.getName(), index);
                return;
            }

            this.bufferInventory.setItem(0, this.offers.get((int) index).getSellStack());
        } else {
            NumismaticOverhaul.CHANNEL.sendToServer(new ShopScreenHandlerRequestC2SPacket(ShopScreenHandlerRequestC2SPacket.Action.LOAD_OFFER, index));
        }
    }

    public void createOffer(long price) {
        if (!this.owner.level.isClientSide) {
            final var stack = bufferInventory.getItem(0);
            if (stack.isEmpty()) return;

            this.shop.addOrReplaceOffer(new ShopOffer(stack, price));
            this.updateClient();
        } else {
            NumismaticOverhaul.CHANNEL.sendToServer(new ShopScreenHandlerRequestC2SPacket(ShopScreenHandlerRequestC2SPacket.Action.CREATE_OFFER, price));
        }
    }

    public void extractCurrency() {
        if (!this.owner.level.isClientSide) {
            ModComponents.CURRENCY.get(owner).modify(shop.getStoredCurrency());
            this.shop.setStoredCurrency(0);
            this.updateClient();
        } else {
            NumismaticOverhaul.CHANNEL.sendToServer(new ShopScreenHandlerRequestC2SPacket(ShopScreenHandlerRequestC2SPacket.Action.EXTRACT_CURRENCY));
        }
    }

    public void deleteOffer() {
        if (!this.owner.level.isClientSide) {
            this.shop.deleteOffer(bufferInventory.getItem(0));
            this.updateClient();
        } else {
            NumismaticOverhaul.CHANNEL.sendToServer(new ShopScreenHandlerRequestC2SPacket(ShopScreenHandlerRequestC2SPacket.Action.DELETE_OFFER));
        }
    }

    public void toggleTransfer() {
        if (!this.owner.level.isClientSide) {
            this.shop.toggleTransfer();
            this.updateClient();
        } else {
            NumismaticOverhaul.CHANNEL.sendToServer(new ShopScreenHandlerRequestC2SPacket(ShopScreenHandlerRequestC2SPacket.Action.TOGGLE_TRANSFER));
        }
    }

    private void updateClient() {
        NumismaticOverhaul.CHANNEL.sendTo(new UpdateShopScreenS2CPacket(shop), (ServerPlayer) owner);
    }

    public ItemStack getBufferStack() {
        return bufferInventory.getItem(0);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
//        return ScreenUtils.handleSlotTransfer(this, invSlot, this.shopInventory.getContainerSize());
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (invSlot < this.shopInventory.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.shopInventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.shopInventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    private static class AutoHidingSlot extends Slot {

        private final int targetTab;
        private final boolean hide;

        public AutoHidingSlot(Container inventory, int index, int x, int y, int targetTab, boolean hide) {
            super(inventory, index, x, y);
            this.targetTab = targetTab;
            this.hide = hide;
        }

        @Override
        public boolean isActive() {
            if (!(Minecraft.getInstance().screen instanceof ShopScreen screen)) return true;
            //noinspection SimplifiableConditionalExpression
            return hide
                    ? screen.tab() != targetTab
                    : screen.tab() == targetTab;
        }
    }
}
