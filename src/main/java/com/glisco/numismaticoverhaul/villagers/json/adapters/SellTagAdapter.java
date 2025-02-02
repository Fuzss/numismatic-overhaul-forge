package com.glisco.numismaticoverhaul.villagers.json.adapters;

import com.glisco.numismaticoverhaul.NumismaticCommand;
import com.glisco.numismaticoverhaul.NumismaticOverhaul;
import com.glisco.numismaticoverhaul.currency.CurrencyHelper;
import com.glisco.numismaticoverhaul.villagers.json.TradeJsonAdapter;
import com.glisco.numismaticoverhaul.villagers.json.VillagerJsonHelper;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;

public class SellTagAdapter extends TradeJsonAdapter {

    @Override
    @NotNull
    public VillagerTrades.ItemListing deserialize(JsonObject json) {
        loadDefaultStats(json, true);

        VillagerJsonHelper.assertJsonObject(json, "sell");

        final var sellObject = GsonHelper.getAsJsonObject(json, "sell");
        final var tag = new ResourceLocation(GsonHelper.getAsString(sellObject, "tag"));
        final int count = GsonHelper.getAsInt(sellObject, "count", 1);

        int price = json.get("price").getAsInt();
        return new Factory(tag, count, price, max_uses, villager_experience, price_multiplier);
    }

    private static class Factory implements VillagerTrades.ItemListing {
        private final ResourceLocation sellTag;
        private final int count;
        private final int maxUses;
        private final int experience;
        private final int price;
        private final float multiplier;

        public Factory(ResourceLocation sellTag, int count, int price, int maxUses, int experience, float multiplier) {
            this.sellTag = sellTag;
            this.count = count;
            this.maxUses = maxUses;
            this.experience = experience;
            this.price = price;
            this.multiplier = multiplier;
        }

        public MerchantOffer getOffer(Entity entity, RandomSource random) {
            final var entries = Registry.ITEM.getTag(TagKey.create(Registry.ITEM_REGISTRY, sellTag))
                    .orElse(null);

            if (entries == null) {
                NumismaticOverhaul.LOGGER.warn("Could not generate trade for tag '" + sellTag + "', as it does not exist");

                final var player = entity.level.getNearestPlayer(entity, 15);
                if (player != null) {
                    player.displayClientMessage(NumismaticCommand.toChatMessage("there has been a problem generating trades, check the log for details"), false);
                }

                return null;
            }

            final var sellStack = new ItemStack(entries.get(random.nextInt(entries.size())).value(), this.count);
            return new MerchantOffer(CurrencyHelper.getClosest(price), sellStack, this.maxUses, this.experience, multiplier);
        }
    }

}
