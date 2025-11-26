package com.mrcrayfish.backpacked.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.UUID;

public record ShelfKey(ResourceKey<Level> level, long position)
{
    public ShelfKey(ResourceKey<Level> level, BlockPos pos)
    {
        this(level, pos.asLong());
    }

    public static final Codec<ShelfKey> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ResourceKey.codec(Registries.DIMENSION).fieldOf("key").forGetter(ShelfKey::level),
        Codec.LONG.fieldOf("position").forGetter(ShelfKey::position)
    ).apply(instance, ShelfKey::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShelfKey> STREAM_CODEC = StreamCodec.composite(
        ResourceKey.streamCodec(Registries.DIMENSION), ShelfKey::level,
        ByteBufCodecs.VAR_LONG, ShelfKey::position,
        ShelfKey::new
    );
}
