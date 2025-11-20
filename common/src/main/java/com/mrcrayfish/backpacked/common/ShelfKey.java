package com.mrcrayfish.backpacked.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record ShelfKey(ResourceKey<Level> level, UUID id)
{
    public static final Codec<ShelfKey> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceKey.codec(Registries.DIMENSION).fieldOf("key").forGetter(ShelfKey::level),
        UUIDUtil.CODEC.fieldOf("id").forGetter(ShelfKey::id)
    ).apply(instance, ShelfKey::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShelfKey> STREAM_CODEC = StreamCodec.composite(
        ResourceKey.streamCodec(Registries.DIMENSION), ShelfKey::level,
        UUIDUtil.STREAM_CODEC, ShelfKey::id,
        ShelfKey::new
    );
}
