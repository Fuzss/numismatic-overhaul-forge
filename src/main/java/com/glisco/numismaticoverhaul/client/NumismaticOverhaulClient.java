package com.glisco.numismaticoverhaul.client;

import com.glisco.numismaticoverhaul.NumismaticOverhaul;
import com.glisco.numismaticoverhaul.block.NumismaticOverhaulBlocks;
import com.glisco.numismaticoverhaul.client.gui.CurrencyTooltipComponent;
import com.glisco.numismaticoverhaul.client.gui.PiggyBankScreen;
import com.glisco.numismaticoverhaul.client.gui.ShopScreen;
import com.glisco.numismaticoverhaul.item.CurrencyTooltipData;
import com.glisco.numismaticoverhaul.item.MoneyBagItem;
import com.glisco.numismaticoverhaul.item.NumismaticOverhaulItems;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NumismaticOverhaul.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NumismaticOverhaulClient {

    @SubscribeEvent
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers evt) {
        evt.registerBlockEntityRenderer(NumismaticOverhaulBlocks.SHOP_BLOCK_ENTITY_TYPE.get(), ShopBlockEntityRender::new);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(final RegisterClientTooltipComponentFactoriesEvent evt) {
        evt.register(CurrencyTooltipData.class, CurrencyTooltipComponent::new);
    }

    @SubscribeEvent
    public static void onConstructMod(final FMLClientSetupEvent evt) {
        MenuScreens.register(NumismaticOverhaul.SHOP_SCREEN_HANDLER_TYPE.get(), ShopScreen::new);
        MenuScreens.register(NumismaticOverhaul.PIGGY_BANK_SCREEN_HANDLER_TYPE.get(), PiggyBankScreen::new);

        ItemProperties.register(NumismaticOverhaulItems.BRONZE_COIN.get(), new ResourceLocation("coins"), (stack, world, entity, seed) -> stack.getCount() / 100.0f);
        ItemProperties.register(NumismaticOverhaulItems.SILVER_COIN.get(), new ResourceLocation("coins"), (stack, world, entity, seed) -> stack.getCount() / 100.0f);
        ItemProperties.register(NumismaticOverhaulItems.GOLD_COIN.get(), new ResourceLocation("coins"), (stack, world, entity, seed) -> stack.getCount() / 100.0f);

        ItemProperties.register(NumismaticOverhaulItems.MONEY_BAG.get(), new ResourceLocation("size"), (stack, world, entity, seed) -> {
            long[] values = ((MoneyBagItem) NumismaticOverhaulItems.MONEY_BAG.get()).getCombinedValue(stack);
            if (values.length < 3) return 0;

            if (values[2] > 0) return 1;
            if (values[1] > 0) return .5f;

            return 0;
        });

//        TooltipComponentCallback.EVENT.register(data -> {
//            if (!(data instanceof CurrencyTooltipData currencyData)) return null;
//            return new CurrencyTooltipComponent(currencyData);
//        });

//        BlockEntityRendererRegistry.register(NumismaticOverhaulBlocks.Entities.SHOP, ShopBlockEntityRender::new);
    }

}
