//package com.glisco.numismaticoverhaul.mixin;
//
//import com.glisco.numismaticoverhaul.villagers.data.NumismaticVillagerTradesRegistry;
//import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
//import net.fabricmc.fabric.impl.object.builder.TradeOfferInternals;
//import net.minecraft.world.entity.npc.VillagerProfession;
//import net.minecraft.world.entity.npc.VillagerTrades;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Overwrite;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.function.Consumer;
//
//@Mixin(TradeOfferInternals.class)
//public class TradeOfferInternalsMixin {
//
//    /**
//     * Redirect Fabric API trade helper to integrate with NO
//     *
//     * @author glisco
//     */
//    @Overwrite(remap = false)
//    public static synchronized void registerVillagerOffers(VillagerProfession profession, int level, Consumer<List<VillagerTrades.ItemListing>> factory) {
//        final var factories = new ArrayList<VillagerTrades.ItemListing>();
//        factory.accept(factories);
//
//        NumismaticVillagerTradesRegistry.registerFabricVillagerTrades(profession, level, factories);
//    }
//
//    /**
//     * Redirect Fabric API trade helper to integrate with NO
//     *
//     * @author glisco
//     */
//    @Overwrite(remap = false)
//    public static synchronized void registerWanderingTraderOffers(int level, Consumer<List<VillagerTrades.ItemListing>> factory) {
//        final var factories = new ArrayList<VillagerTrades.ItemListing>();
//        factory.accept(factories);
//
//        NumismaticVillagerTradesRegistry.registerFabricWanderingTraderTrades(level, factories);
//    }
//
//    /**
//     * Disable registering trades completely to make sure that no one ever interferes with the NO system
//     *
//     * @author glisco
//     */
//    @Overwrite(remap = false)
//    private static void registerOffers(Int2ObjectMap<VillagerTrades.ItemListing[]> leveledTradeMap, int level, Consumer<List<VillagerTrades.ItemListing>> factory) {
//    }
//}
