package com.glisco.numismaticoverhaul.item;

import net.minecraft.world.item.ItemStack;

public interface CurrencyItem {

    String ORIGINAL_VALUE = "OriginalValue";

    static void setOriginalValue(ItemStack stack, long value) {
        stack.getOrCreateTag().putLong(ORIGINAL_VALUE, value);
    }

    static long getOriginalValue(ItemStack stack) {
        return stack.getOrCreateTag().getLong(ORIGINAL_VALUE);
    }

    static boolean hasOriginalValue(ItemStack stack) {
        return stack.getOrCreateTag().contains(ORIGINAL_VALUE);
    }

    boolean wasAdjusted(ItemStack other);

    long getValue(ItemStack stack);

    long[] getCombinedValue(ItemStack stack);

}
