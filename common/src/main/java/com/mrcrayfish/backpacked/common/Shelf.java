package com.mrcrayfish.backpacked.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.augment.impl.RecallAugment;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record Shelf(ResourceKey<Level> key, UUID id)
{
    public static final Codec<Shelf> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceKey.codec(Registries.DIMENSION).fieldOf("key").forGetter(Shelf::key),
        UUIDUtil.CODEC.fieldOf("id").forGetter(Shelf::id)
    ).apply(instance, Shelf::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Shelf> STREAM_CODEC = StreamCodec.composite(
        ResourceKey.streamCodec(Registries.DIMENSION), Shelf::key,
        UUIDUtil.STREAM_CODEC, Shelf::id,
        Shelf::new
    );
}
