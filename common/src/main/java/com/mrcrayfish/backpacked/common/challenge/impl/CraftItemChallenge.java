package com.mrcrayfish.backpacked.common.challenge.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.BackpackedCodecs;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class CraftItemChallenge extends Challenge
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "craft_item");
    public static final Serializer SERIALIZER = new Serializer();

    private final ProgressFormatter formatter;
    private final Optional<CraftedItemPredicate> predicate;
    private final int count;

    public CraftItemChallenge(ProgressFormatter formatter, Optional<CraftedItemPredicate> predicate, int count)
    {
        super(ID);
        this.formatter = formatter;
        this.predicate = predicate;
        this.count = count;
    }

    @Override
    public ChallengeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    public IProgressTracker createProgressTracker(ResourceLocation backpackId)
    {
        return new Tracker(this.formatter, this.predicate, this.count);
    }

    public static class Serializer extends ChallengeSerializer<CraftItemChallenge>
    {
        @Override
        public CraftItemChallenge deserialize(JsonObject object)
        {
            ProgressFormatter formatter = readFormatter(object, ProgressFormatter.CRAFT_X_OF_X);
            Optional<CraftedItemPredicate> predicate = CraftedItemPredicate.deserialize(object.get("crafted_item"));
            int count = readCount(object, 1);
            return new CraftItemChallenge(formatter, predicate, count);
        }
    }

    public static class Tracker extends CountProgressTracker
    {
        private final Optional<CraftedItemPredicate> predicate;

        public Tracker(ProgressFormatter formatter, Optional<CraftedItemPredicate> predicate, int maxCount)
        {
            super(maxCount, formatter);
            this.predicate = predicate;
        }

        public static void registerEvent()
        {
            PlayerEvents.CRAFT_ITEM.register((player, stack, inventory) -> {
                if(player.level().isClientSide()) return;
                UnlockManager.getTrackers(player, Tracker.class).forEach(tracker -> {
                    if(tracker.isComplete()) return;
                    if(tracker.predicate.map(p -> p.test(stack)).orElse(true))
                    {
                        tracker.increment(stack.getCount(), (ServerPlayer) player);
                    }
                });
            });
        }
    }

    public record CraftedItemPredicate(Optional<Set<String>> modIds, Optional<TagKey<Item>> tag,
                                       Optional<HolderSet<Item>> items)
    {
        public boolean test(ItemStack stack)
        {
            if(this.modIds.isPresent())
            {
                ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
                if(this.modIds.get().contains(key.getNamespace()))
                {
                    return true;
                }
            }
            if(this.tag.isPresent())
            {
                if(stack.is(this.tag.get()))
                {
                    return true;
                }
            }
            if(this.items.isPresent())
            {
                if(this.items.get().contains(stack.getItemHolder()))
                {
                    return true;
                }
            }
            return false;
        }

        public static Optional<CraftedItemPredicate> deserialize(@Nullable JsonElement element)
        {
            if(element == null || !element.isJsonObject()) return Optional.empty();
            JsonObject object = element.getAsJsonObject();
            Optional<Set<String>> namespaces = object.has("namespace") ? BackpackedCodecs.STRING_SET.parse(JsonOps.INSTANCE, object.get("namespace"))
                .result() : Optional.empty();
            Optional<TagKey<Item>> tag = object.has("tag") ? TagKey.codec(Registries.ITEM)
                .parse(JsonOps.INSTANCE, object.get("tag"))
                .result() : Optional.empty();
            Optional<HolderSet<Item>> items = object.has("items") ? BackpackedCodecs.ITEMS.parse(JsonOps.INSTANCE, object.get("items"))
                .result() : Optional.empty();
            return Optional.of(new CraftedItemPredicate(namespaces, tag, items));
        }
    }
}