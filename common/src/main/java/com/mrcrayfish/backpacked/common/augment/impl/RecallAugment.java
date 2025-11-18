package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record RecallAugment(Optional<ShelfPosition> shelf) implements Augment<RecallAugment>
{
    public static final AugmentType<RecallAugment> TYPE = new AugmentType<>(
        Utils.rl("recall"),
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            ShelfPosition.CODEC.optionalFieldOf("key").forGetter(RecallAugment::shelf)
        ).apply(instance, RecallAugment::new)),
        StreamCodec.composite(
            ByteBufCodecs.optional(ShelfPosition.STREAM_CODEC), RecallAugment::shelf,
            RecallAugment::new
        ),
        () -> new RecallAugment(Optional.empty())
    );

    @Override
    public AugmentType<RecallAugment> type()
    {
        return TYPE;
    }

    public RecallAugment setShelfPosition(ResourceKey<Level> key, BlockPos pos)
    {
        return new RecallAugment(Optional.of(new ShelfPosition(key, pos)));
    }

    public record ShelfPosition(ResourceKey<Level> key, BlockPos pos)
    {
        private static final Codec<ShelfPosition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("key").forGetter(ShelfPosition::key),
            BlockPos.CODEC.fieldOf("pos").forGetter(ShelfPosition::pos)
        ).apply(instance, ShelfPosition::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, ShelfPosition> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION), ShelfPosition::key,
            BlockPos.STREAM_CODEC, ShelfPosition::pos,
            ShelfPosition::new
        );
    }
}
