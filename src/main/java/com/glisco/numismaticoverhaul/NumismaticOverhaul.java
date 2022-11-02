package com.glisco.numismaticoverhaul;

import com.glisco.numismaticoverhaul.block.NumismaticOverhaulBlocks;
import com.glisco.numismaticoverhaul.block.PiggyBankScreenHandler;
import com.glisco.numismaticoverhaul.block.ShopScreenHandler;
import com.glisco.numismaticoverhaul.currency.CurrencyComponent;
import com.glisco.numismaticoverhaul.currency.MoneyBagLootEntry;
import com.glisco.numismaticoverhaul.item.MoneyBagItem;
import com.glisco.numismaticoverhaul.item.NumismaticOverhaulItems;
import com.glisco.numismaticoverhaul.network.RequestPurseActionC2SPacket;
import com.glisco.numismaticoverhaul.network.ShopScreenHandlerRequestC2SPacket;
import com.glisco.numismaticoverhaul.network.UpdateShopScreenS2CPacket;
import com.glisco.numismaticoverhaul.villagers.data.VillagerTradesResourceListener;
import com.glisco.numismaticoverhaul.villagers.json.VillagerTradesHandler;
import fuzs.puzzleslib.capability.ForgeCapabilityController;
import fuzs.puzzleslib.config.ConfigHolder;
import fuzs.puzzleslib.core.CommonFactories;
import fuzs.puzzleslib.init.CommonGameRuleFactory;
import fuzs.puzzleslib.init.RegistryManager;
import fuzs.puzzleslib.init.RegistryReference;
import fuzs.puzzleslib.network.MessageDirection;
import fuzs.puzzleslib.network.NetworkHandler;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NumismaticOverhaul.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NumismaticOverhaul {

    public static final String MOD_ID = "numismaticoverhaul";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final NetworkHandler CHANNEL = CommonFactories.INSTANCE.network(MOD_ID);
//    private static final ParticleSystemController PARTICLE_SYSTEMS = new ParticleSystemController(id("particles"));
//    public static final ParticleSystem<Integer> PIGGY_BANK_BROKEN = PARTICLE_SYSTEMS.register(Integer.class, (world, pos, data) -> {
//        ClientParticles.setParticleCount(6 * data);
//        ClientParticles.randomizeVelocity(2);
//        ClientParticles.spawnCenteredOnBlock(
//                new BlockParticleOption(ParticleTypes.BLOCK, NumismaticOverhaulBlocks.PIGGY_BANK.defaultBlockState()),
//                world, new BlockPos(pos), .75
//        );
//    });

    private static final RegistryManager REGISTRY = CommonFactories.INSTANCE.registration(NumismaticOverhaul.MOD_ID);
    public static final RegistryReference<MenuType<ShopScreenHandler>> SHOP_SCREEN_HANDLER_TYPE = REGISTRY.registerMenuTypeSupplier("shop", () -> ShopScreenHandler::new);
    public static final RegistryReference<MenuType<PiggyBankScreenHandler>> PIGGY_BANK_SCREEN_HANDLER_TYPE = REGISTRY.registerMenuTypeSupplier("piggy_bank", () -> PiggyBankScreenHandler::new);

    public static final RegistryReference<SoundEvent> PIGGY_BANK_BREAK = REGISTRY.registerRawSoundEvent("piggy_bank_break");
    public static final RegistryReference<LootPoolEntryType> MONEY_BAG_ENTRY = REGISTRY.register(Registry.LOOT_ENTRY_REGISTRY, "money_bag", () -> new LootPoolEntryType(new MoneyBagLootEntry.Serializer()));

    public static final TagKey<EntityType<?>> THE_BOURGEOISIE = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, id("the_bourgeoisie"));
    public static final TagKey<Block> VERY_HEAVY_BLOCKS = TagKey.create(Registry.BLOCK_REGISTRY, id("very_heavy_blocks"));

    public static final GameRules.Key<GameRules.IntegerValue> MONEY_DROP_PERCENTAGE = CommonGameRuleFactory.INSTANCE.register("moneyDropPercentage", GameRules.Category.PLAYER, CommonGameRuleFactory.INSTANCE.createIntRule(10));

    public static final CreativeModeTab NUMISMATIC_GROUP = new CreativeModeTab("numismaticoverhaul.main") {
        @Override
        public ItemStack makeIcon() {
            return MoneyBagItem.createCombined(new long[]{0, 1, 0});
        }
    };

    public static final ConfigHolder CONFIG = CommonFactories.INSTANCE.clientConfig(NumismaticOverhaulConfigModel.class, () -> new NumismaticOverhaulConfigModel());

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt_) {
        CONFIG.bakeConfigs(MOD_ID);
        ModComponents.touch();
        ForgeCapabilityController.setCapabilityToken(ModComponents.CURRENCY, new CapabilityToken<CurrencyComponent>() {});

        NumismaticOverhaulItems.touch();
        NumismaticOverhaulBlocks.touch();
//        FieldRegistrationHandler.register(NumismaticOverhaulItems.class, MOD_ID, false);
//        FieldRegistrationHandler.register(NumismaticOverhaulBlocks.class, MOD_ID, false);
//        FieldRegistrationHandler.register(NumismaticOverhaulBlocks.Entities.class, MOD_ID, false);

//        Registry.register(Registry.SOUND_EVENT, PIGGY_BANK_BREAK.getId(), PIGGY_BANK_BREAK);
//        Registry.register(Registry.LOOT_POOL_ENTRY_TYPE, id("money_bag"), MONEY_BAG_ENTRY);
//
//        Registry.register(Registry.SCREEN_HANDLER, id("shop"), SHOP_SCREEN_HANDLER_TYPE);
//        Registry.register(Registry.SCREEN_HANDLER, id("piggy_bank"), PIGGY_BANK_SCREEN_HANDLER_TYPE);

        CHANNEL.register(RequestPurseActionC2SPacket.class, RequestPurseActionC2SPacket::new, MessageDirection.TO_SERVER);
        CHANNEL.register(ShopScreenHandlerRequestC2SPacket.class, ShopScreenHandlerRequestC2SPacket::new, MessageDirection.TO_SERVER);
        CHANNEL.register(UpdateShopScreenS2CPacket.class, UpdateShopScreenS2CPacket::new, MessageDirection.TO_CLIENT);
//        UpdateShopScreenS2CPacket.initialize();

        MinecraftForge.EVENT_BUS.addListener((final AddReloadListenerEvent evt) -> {
            evt.addListener(new VillagerTradesResourceListener());
        });

//        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new VillagerTradesResourceListener());
        VillagerTradesHandler.registerDefaultAdapters();

//        CommandRegistrationCallback.EVENT.register(NumismaticCommand::register);

//        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> {
//            VillagerTradesHandler.broadcastErrors(server);
//        });

        MinecraftForge.EVENT_BUS.addListener((final OnDatapackSyncEvent evt) -> {
            VillagerTradesHandler.broadcastErrors(evt.getPlayer().server);
        });

//        NUMISMATIC_GROUP.initialize();

//        if (CONFIG.get(NumismaticOverhaulConfigModel.class).generateCurrencyInChests) {
//            LootOps.injectItem(NumismaticOverhaulItems.GOLD_COIN, .01f, BuiltInLootTables.STRONGHOLD_LIBRARY, BuiltInLootTables.BASTION_TREASURE, BuiltInLootTables.STRONGHOLD_CORRIDOR,
//                    BuiltInLootTables.PILLAGER_OUTPOST, BuiltInLootTables.BURIED_TREASURE, BuiltInLootTables.SIMPLE_DUNGEON, BuiltInLootTables.ABANDONED_MINESHAFT);
//
//            LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
//                if (anyMatch(id, BuiltInLootTables.DESERT_PYRAMID)) {
//                    tableBuilder.withPool(LootPool.lootPool().add(MoneyBagLootEntry.builder(CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.desertMinLoot, CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.desertMaxLoot))
//                            .conditionally(LootItemRandomChanceCondition.randomChance(0.45f)));
//                } else if (anyMatch(id, BuiltInLootTables.SIMPLE_DUNGEON, BuiltInLootTables.ABANDONED_MINESHAFT)) {
//                    tableBuilder.withPool(LootPool.lootPool().add(MoneyBagLootEntry.builder(CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.dungeonMinLoot, CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.dungeonMaxLoot))
//                            .conditionally(LootItemRandomChanceCondition.randomChance(0.75f)));
//                } else if (anyMatch(id, BuiltInLootTables.BASTION_TREASURE, BuiltInLootTables.STRONGHOLD_CORRIDOR, BuiltInLootTables.PILLAGER_OUTPOST, BuiltInLootTables.BURIED_TREASURE)) {
//                    tableBuilder.withPool(LootPool.lootPool().add(MoneyBagLootEntry.builder(CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.structureMinLoot, CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.structureMaxLoot))
//                            .conditionally(LootItemRandomChanceCondition.randomChance(0.75f)));
//                } else if (anyMatch(id, BuiltInLootTables.STRONGHOLD_LIBRARY)) {
//                    tableBuilder.withPool(LootPool.lootPool().add(MoneyBagLootEntry.builder(CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.strongholdLibraryMinLoot, CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.strongholdLibraryMaxLoot))
//                            .conditionally(LootItemRandomChanceCondition.randomChance(0.85f)));
//                }
//            });
//        }

        MinecraftForge.EVENT_BUS.addListener((final LootTableLoadEvent evt) -> {
            ResourceLocation id = evt.getName();
            if (anyMatch(id, BuiltInLootTables.STRONGHOLD_LIBRARY, BuiltInLootTables.BASTION_TREASURE, BuiltInLootTables.STRONGHOLD_CORRIDOR, BuiltInLootTables.PILLAGER_OUTPOST, BuiltInLootTables.BURIED_TREASURE, BuiltInLootTables.SIMPLE_DUNGEON, BuiltInLootTables.ABANDONED_MINESHAFT)) {
                evt.getTable().addPool(LootPool.lootPool().add(LootItem.lootTableItem(NumismaticOverhaulItems.GOLD_COIN.get()).when(LootItemRandomChanceCondition.randomChance(0.01f))).build());
            }
            if (anyMatch(id, BuiltInLootTables.DESERT_PYRAMID)) {
                evt.getTable().addPool(LootPool.lootPool().add(MoneyBagLootEntry.builder(CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.desertMinLoot, CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.desertMaxLoot))
                        .when(LootItemRandomChanceCondition.randomChance(0.45f)).build());
            } else if (anyMatch(id, BuiltInLootTables.SIMPLE_DUNGEON, BuiltInLootTables.ABANDONED_MINESHAFT)) {
                evt.getTable().addPool(LootPool.lootPool().add(MoneyBagLootEntry.builder(CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.dungeonMinLoot, CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.dungeonMaxLoot))
                        .when(LootItemRandomChanceCondition.randomChance(0.75f)).build());
            } else if (anyMatch(id, BuiltInLootTables.BASTION_TREASURE, BuiltInLootTables.STRONGHOLD_CORRIDOR, BuiltInLootTables.PILLAGER_OUTPOST, BuiltInLootTables.BURIED_TREASURE)) {
                evt.getTable().addPool(LootPool.lootPool().add(MoneyBagLootEntry.builder(CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.structureMinLoot, CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.structureMaxLoot))
                        .when(LootItemRandomChanceCondition.randomChance(0.75f)).build());
            } else if (anyMatch(id, BuiltInLootTables.STRONGHOLD_LIBRARY)) {
                evt.getTable().addPool(LootPool.lootPool().add(MoneyBagLootEntry.builder(CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.strongholdLibraryMinLoot, CONFIG.get(NumismaticOverhaulConfigModel.class).lootOptions.strongholdLibraryMaxLoot))
                        .when(LootItemRandomChanceCondition.randomChance(0.85f)).build());
            }
        });

        MinecraftForge.EVENT_BUS.addListener((final RegisterCommandsEvent evt) -> {
            NumismaticCommand.register(evt.getDispatcher(), evt.getBuildContext(), evt.getCommandSelection());
        });

    }

    private static boolean anyMatch(ResourceLocation target, ResourceLocation... comparisons) {
        for (ResourceLocation comparison : comparisons) {
            if (target.equals(comparison)) return true;
        }
        return false;
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
