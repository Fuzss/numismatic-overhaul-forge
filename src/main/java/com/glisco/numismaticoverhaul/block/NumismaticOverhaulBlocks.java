package com.glisco.numismaticoverhaul.block;

import com.glisco.numismaticoverhaul.NumismaticOverhaul;
import com.glisco.numismaticoverhaul.item.CurrencyTooltipData;
import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.init.RegistryManager;
import fuzs.puzzleslib.init.RegistryReference;
import fuzs.puzzleslib.init.builder.ModBlockEntityTypeBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class NumismaticOverhaulBlocks {
    private static final RegistryManager REGISTRY = CommonFactories.INSTANCE.registration(NumismaticOverhaul.MOD_ID);

    public static final RegistryReference<Block> SHOP_BLOCK = REGISTRY.registerBlockWithItem("shop", () -> new ShopBlock(false), NumismaticOverhaul.NUMISMATIC_GROUP);
    public static final RegistryReference<Block> INEXHAUSTIBLE_SHOP_BLOCK = REGISTRY.registerBlock("inexhaustible_shop", () -> new ShopBlock(true));
    public static final RegistryReference<Block> PIGGY_BANK_BLOCK = REGISTRY.registerBlock("piggy_bank", () -> new PiggyBankBlock());
    public static final RegistryReference<Item> INEXHAUSTIBLE_SHOP_ITEM = REGISTRY.registerItem("inexhaustible_shop", () -> new BlockItem(INEXHAUSTIBLE_SHOP_BLOCK.get(), new Item.Properties().tab(NumismaticOverhaul.NUMISMATIC_GROUP).rarity(Rarity.EPIC)) {
        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
            tooltip.add(Component.translatable(stack.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
        }
    });
    public static final RegistryReference<Item> PIGGY_BANK_ITEM = REGISTRY.registerItem("piggy_bank", () -> new BlockItem(PIGGY_BANK_BLOCK.get(), new Item.Properties().tab(NumismaticOverhaul.NUMISMATIC_GROUP)) {
        @Override
        public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
            if (stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
                var items = NonNullList.withSize(3, ItemStack.EMPTY);
                ContainerHelper.loadAllItems(stack.getTagElement("BlockEntityTag"), items);

                var values = new long[]{items.get(0).getCount(), items.get(1).getCount(), items.get(2).getCount()};
                return Optional.of(new CurrencyTooltipData(values, new long[]{-1}));
            } else {
                return Optional.empty();
            }
        }

        @Override
        public @NotNull EquipmentSlot getEquipmentSlot(ItemStack stack) {
            return EquipmentSlot.HEAD;
        }
    });

    public static final RegistryReference<BlockEntityType<ShopBlockEntity>> SHOP_BLOCK_ENTITY_TYPE = REGISTRY.registerBlockEntityTypeBuilder("shop", () -> ModBlockEntityTypeBuilder.of(ShopBlockEntity::new, NumismaticOverhaulBlocks.SHOP_BLOCK.get(), INEXHAUSTIBLE_SHOP_BLOCK.get()));

    public static final RegistryReference<BlockEntityType<PiggyBankBlockEntity>> PIGGY_BANK_BLOCK_ENTITY_TYPE = REGISTRY.registerBlockEntityTypeBuilder("piggy_bank", () -> ModBlockEntityTypeBuilder.of(PiggyBankBlockEntity::new, NumismaticOverhaulBlocks.PIGGY_BANK_BLOCK.get()));

    public static void touch() {}

//    @Override
//    public BlockItem createBlockItem(Block block, String identifier) {
//        if (block == INEXHAUSTIBLE_SHOP) {
//            return new BlockItem(block, new Item.Properties().tab(NumismaticOverhaul.NUMISMATIC_GROUP).rarity(Rarity.EPIC)) {
//                @Override
//                public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
//                    tooltip.add(Component.translatable(stack.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
//                }
//            };
//        } else if (block == PIGGY_BANK) {
//            return new BlockItem(block, new Item.Properties().tab(NumismaticOverhaul.NUMISMATIC_GROUP)) {
//                @Override
//                public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
//                    if (stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
//                        var items = NonNullList.withSize(3, ItemStack.EMPTY);
//                        ContainerHelper.loadAllItems(stack.getTagElement("BlockEntityTag"), items);
//
//                        var values = new long[]{items.get(0).getCount(), items.get(1).getCount(), items.get(2).getCount()};
//                        return Optional.of(new CurrencyTooltipData(values, new long[]{-1}));
//                    } else {
//                        return Optional.empty();
//                    }
//                }
//
//                @Override
//                public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
//                    return EquipmentSlot.HEAD;
//                }
//            };
//        }
//
//        return new BlockItem(block, new Item.Properties().tab(NumismaticOverhaul.NUMISMATIC_GROUP));
//    }
//
//    public static final class Entities implements AutoRegistryContainer<BlockEntityType<?>> {
//
//        @Override
//        public Registry<BlockEntityType<?>> getRegistry() {
//            return Registry.BLOCK_ENTITY_TYPE;
//        }
//
//        @Override
//        public Class<BlockEntityType<?>> getTargetFieldType() {
//            //noinspection unchecked
//            return (Class<BlockEntityType<?>>) (Object) BlockEntityType.class;
//        }
//    }
}
