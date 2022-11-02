package com.glisco.numismaticoverhaul.block;

import com.glisco.numismaticoverhaul.NumismaticOverhaul;
import com.glisco.numismaticoverhaul.item.NumismaticOverhaulItems;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PiggyBankScreenHandler extends AbstractContainerMenu {

    private final ContainerLevelAccess context;

    public PiggyBankScreenHandler(int index, Inventory playerInventory) {
        this(index, playerInventory, ContainerLevelAccess.NULL, new SimpleContainer(3));
    }

    public PiggyBankScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context, Container piggyBankInventory) {
        super(NumismaticOverhaul.PIGGY_BANK_SCREEN_HANDLER_TYPE.get(), syncId);
        this.context = context;

//        this.addSlot(new ValidatingSlot(piggyBankInventory, 0, 62, 26, stack -> stack.is(NumismaticOverhaulItems.BRONZE_COIN)));
//        this.addSlot(new ValidatingSlot(piggyBankInventory, 1, 80, 26, stack -> stack.is(NumismaticOverhaulItems.SILVER_COIN)));
//        this.addSlot(new ValidatingSlot(piggyBankInventory, 2, 98, 26, stack -> stack.is(NumismaticOverhaulItems.GOLD_COIN)));

        this.addSlot(new Slot(piggyBankInventory, 0, 62, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(NumismaticOverhaulItems.BRONZE_COIN.get());
            }
        });
        this.addSlot(new Slot(piggyBankInventory, 1, 80, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(NumismaticOverhaulItems.SILVER_COIN.get());
            }
        });
        this.addSlot(new Slot(piggyBankInventory, 2, 98, 26) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(NumismaticOverhaulItems.GOLD_COIN.get());
            }
        });

//        SlotGenerator.begin(this::addSlot, 8, 63).playerInventory(playerInventory);

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 63 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 121));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
//        return ScreenUtils.handleSlotTransfer(this, index, 3);
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 3) {
                if (!this.moveItemStackTo(itemstack1, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
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

    @Override
    public boolean stillValid(Player player) {
        return stillValid(context, player, NumismaticOverhaulBlocks.PIGGY_BANK_BLOCK.get());
    }
}
