package com.glisco.numismaticoverhaul.currency;

import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.item.CoinItem;
import fuzs.puzzleslib.capability.data.CapabilityComponent;
import fuzs.puzzleslib.capability.data.SyncedCapabilityComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CurrencyComponent implements SyncedCapabilityComponent {

    private boolean dirty;
    private long value;
    private final Player provider;

    private final List<Long> transactions;

    public CurrencyComponent(Player provider) {
        this.provider = provider;
        this.transactions = new ArrayList<Long>();
    }

    @Override
    public void read(CompoundTag tag) {
        value = tag.getLong("Value");
    }

    @Override
    public void write(CompoundTag tag) {
        tag.putLong("Value", value);
    }

    public long getValue() {
        return value;
    }

    /**
     * This is only to be used in specific edge cases
     * <br>
     * Use {@link CurrencyComponent#modify(long)} or the transaction system wherever possible
     */
    @Deprecated
    public void setValue(long value) {
        this.markDirty();
        this.value = value;

        //Update Client
        if (!provider.level.isClientSide) {
            ModComponents.CURRENCY.syncToRemote((ServerPlayer) this.provider);
        }
    }

    /**
     * Modifies this component, displays a message in the action bar
     *
     * @param value The value to modify by
     */
    public void modify(long value) {
        setValue(this.value + value);

        long tempValue = value < 0 ? -value : value;

        List<ItemStack> transactionStacks = CurrencyConverter.getAsItemStackList(tempValue);
        if (transactionStacks.isEmpty()) return;

        MutableComponent message = value < 0 ? net.minecraft.network.chat.Component.literal("§c- ") : net.minecraft.network.chat.Component.literal("§a+ ");
        message.append(net.minecraft.network.chat.Component.literal("§7["));
        for (ItemStack stack : transactionStacks) {
            message.append(net.minecraft.network.chat.Component.literal("§b" + stack.getCount() + " "));
            message.append(net.minecraft.network.chat.Component.translatable("currency.numismaticoverhaul." + ((CoinItem) stack.getItem()).currency.name().toLowerCase()));
            if (transactionStacks.indexOf(stack) != transactionStacks.size() - 1) message.append(net.minecraft.network.chat.Component.literal(", "));
        }
        message.append(net.minecraft.network.chat.Component.literal("§7]"));

        provider.displayClientMessage(message, true);
    }

    /**
     * Same as {@link CurrencyComponent#modify(long)}, but doesn't show a message in the action bar
     *
     * @param value The value to modify by
     */
    public void silentModify(long value) {
        setValue(this.value + value);
    }

    /**
     * Enqueues a transaction onto the stack
     *
     * @param value The value this component should be modified by
     */
    public void pushTransaction(long value) {
        this.transactions.add(value);
    }

    /**
     * Pops the most recent transaction off the stack
     *
     * @return The transaction that was popped
     */
    public Long popTransaction() {
        return this.transactions.remove(this.transactions.size() - 1);
    }

    /**
     * Commits the transactions on the current stack into the actual component value and pops the entire stack
     * <br>
     * Displays one accumulated action bar message
     */
    public void commitTransactions() {
        this.modify(this.transactions.stream().mapToLong(Long::longValue).sum());
        this.transactions.clear();
    }

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    @Override
    public void markDirty() {
        this.dirty = true;
    }

    @Override
    public void markClean() {
        this.dirty = false;
    }
}
