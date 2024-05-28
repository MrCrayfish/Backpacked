package com.mrcrayfish.backpacked.common.challenge.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
    public static final Codec<CraftItemChallenge> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(ProgressFormatter.CODEC.fieldOf("formatter").orElse(ProgressFormatter.CRAFT_X_OF_X).forGetter(challenge -> {
            return challenge.formatter;
        }), ExtraCodecs.strictOptionalField(CraftedItemPredicate.CODEC, "crafted_item").forGetter(challenge -> {
            return challenge.predicate;
        }), ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "count", 1).forGetter(challenge -> {
            return challenge.count;
        })).apply(builder, CraftItemChallenge::new);
    });

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
        public void write(CraftItemChallenge challenge, FriendlyByteBuf buf)
        {
            buf.writeOptional(challenge.predicate, (buf1, predicate) ->
                buf1.writeNbt(CraftedItemPredicate.CODEC.encodeStart(NbtOps.INSTANCE, predicate)
                    .getOrThrow(false, Constants.LOG::error)));
            buf.writeVarInt(challenge.count);
        }

        @Override
        public CraftItemChallenge read(FriendlyByteBuf buf)
        {
            Optional<CraftedItemPredicate> predicate = buf.readOptional(buf1 -> CraftedItemPredicate.CODEC
                .parse(NbtOps.INSTANCE, buf1.readNbt(NbtAccounter.create(2097152L)))
                .getOrThrow(false, Constants.LOG::error));
            int count = buf.readVarInt();
            return new CraftItemChallenge(ProgressFormatter.CRAFT_X_OF_X, predicate, count);
        }

        @Override
        public Codec<CraftItemChallenge> codec()
        {
            return CraftItemChallenge.CODEC;
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
                if(player.level().isClientSide())
                    return;
                UnlockManager.getTrackers(player, Tracker.class).forEach(tracker -> {
                    if(tracker.isComplete())
                        return;
                    if(tracker.predicate.map(p -> p.test(stack)).orElse(true)) {
                        tracker.increment(stack.getCount(), (ServerPlayer) player);
                    }
                });
            });
        }
    }

    public record CraftedItemPredicate(Optional<Set<String>> modIds, Optional<TagKey<Item>> tag, Optional<HolderSet<Item>> items)
    {
        public static final Codec<CraftedItemPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            ExtraCodecs.strictOptionalField(BackpackedCodecs.STRING_SET, "namespace").forGetter(o -> o.modIds),
            ExtraCodecs.strictOptionalField(TagKey.codec(Registries.ITEM), "tag").forGetter(CraftedItemPredicate::tag),
            ExtraCodecs.strictOptionalField(BackpackedCodecs.ITEMS, "items").forGetter(CraftedItemPredicate::items)
        ).apply(builder, CraftedItemPredicate::new));

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
    }
}
