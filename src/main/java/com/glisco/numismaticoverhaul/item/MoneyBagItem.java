package com.glisco.numismaticoverhaul.item;

import com.glisco.numismaticoverhaul.ModComponents;
import com.glisco.numismaticoverhaul.currency.CurrencyConverter;
import com.glisco.numismaticoverhaul.currency.CurrencyHelper;
import com.glisco.numismaticoverhaul.currency.CurrencyResolver;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class MoneyBagItem extends Item implements CurrencyItem {

    private static final String VALUE = "Value";
    private static final String VALUES = "Values";
    private static final String COMBINED = "Combined";

    public MoneyBagItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public ItemStack getDefaultInstance() {
        var defaultStack = super.getDefaultInstance();
        defaultStack.getOrCreateTag().putLong(VALUE, 0L);
        return defaultStack;
    }

    public static ItemStack create(long value) {
        var stack = new ItemStack(NumismaticOverhaulItems.MONEY_BAG.get());
        stack.getOrCreateTag().putLong(VALUE, value);
        return stack;
    }

    public static ItemStack createCombined(long[] values) {
        var stack = new ItemStack(NumismaticOverhaulItems.MONEY_BAG.get());
        stack.getOrCreateTag().putLongArray(VALUES, values);
        stack.getOrCreateTag().putBoolean(COMBINED, true);
        return stack;
    }

    public long getValue(ItemStack stack) {
        if (stack.getItem() != NumismaticOverhaulItems.MONEY_BAG.get()) return 0;

        if (!stack.getOrCreateTag().contains(COMBINED)) {
            return stack.getOrCreateTag().getLong(VALUE);
        } else {
            return CurrencyResolver.combineValues(CurrencyHelper.getFromNbt(stack.getOrCreateTag(), "Values"));
        }
    }

    @Override
    public long[] getCombinedValue(ItemStack stack) {
        if (!stack.getOrCreateTag().contains(COMBINED)) {
            return CurrencyResolver.splitValues(stack.getOrCreateTag().getLong(VALUE));
        } else {
            return CurrencyHelper.getFromNbt(stack.getOrCreateTag(), "Values");
        }
    }

    public void setValue(ItemStack stack, long value) {
        stack.getOrCreateTag().putLong("Value", value);
    }

    public void setCombinedValue(ItemStack stack, long[] values) {
        stack.getOrCreateTag().putLongArray("Values", values);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack clickedStack, ItemStack otherStack, Slot slot, ClickAction clickType, Player player, SlotAccess cursorStackReference) {
        if (slot instanceof MerchantResultSlot) return false;

        if (clickType == ClickAction.SECONDARY && clickedStack.getItem() == this && otherStack.isEmpty()) {
            final var stackRepresentation = CurrencyConverter.getAsValidStacks(getCombinedValue(clickedStack));
            if (stackRepresentation.isEmpty()) return false;

            final var coinStack = stackRepresentation.get(0);
            cursorStackReference.set(coinStack);

            final long[] values = getCombinedValue(clickedStack);
            values[((CoinItem) coinStack.getItem()).currency.ordinal()] -= coinStack.getCount();

            final long newValue = CurrencyResolver.combineValues(values);
            final boolean canBeCompacted = values[0] < 100 && values[1] < 100 && values[2] < 100;

            if (newValue == 0) {
                slot.set(ItemStack.EMPTY);
            } else if (canBeCompacted && CurrencyConverter.getAsValidStacks(newValue).size() == 1) {
                slot.set(CurrencyConverter.getAsValidStacks(newValue).get(0));
            } else {
                setCombinedValue(clickedStack, values);
            }

        } else if (clickType == ClickAction.PRIMARY) {
            if (!(otherStack.getItem() instanceof CurrencyItem currencyItem)) return false;

            long[] clickedValues = getCombinedValue(clickedStack);
            long[] otherValues = currencyItem.getCombinedValue(otherStack);

            for (int i = 0; i < clickedValues.length; i++) clickedValues[i] += otherValues[i];

            slot.set(MoneyBagItem.createCombined(clickedValues));

            cursorStackReference.set(ItemStack.EMPTY);
        }

        return true;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new CurrencyTooltipData(this.getCombinedValue(stack),
                CurrencyItem.hasOriginalValue(stack) ? CurrencyResolver.splitValues(CurrencyItem.getOriginalValue(stack)) : new long[]{-1}));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (stack.getOrCreateTag().getBoolean(COMBINED)) return;
        if (!(entity instanceof Player player)) return;

        player.getInventory().removeItem(stack);

        for (ItemStack toOffer : CurrencyConverter.getAsValidStacks(getValue(stack))) {
            player.getInventory().placeItemBackInInventory(toOffer);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ModComponents.CURRENCY.get(user).modify(getValue(user.getItemInHand(hand)));
        user.setItemInHand(hand, ItemStack.EMPTY);
        return InteractionResultHolder.success(ItemStack.EMPTY);
    }

    @Override
    public boolean wasAdjusted(ItemStack other) {
        return true;
    }

    @Override
    public Component getDescription() {
        return super.getDescription().copy().setStyle(((CoinItem) NumismaticOverhaulItems.SILVER_COIN.get()).NAME_STYLE);
    }

}
