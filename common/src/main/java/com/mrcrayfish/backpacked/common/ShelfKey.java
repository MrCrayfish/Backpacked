package com.mrcrayfish.backpacked.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.augment.impl.QuiverlinkAugment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

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

    public void encode(FriendlyByteBuf buf)
    {
        buf.writeResourceLocation(this.level.location());
        buf.writeVarLong(this.position);
    }

    public static ShelfKey decode(FriendlyByteBuf buf)
    {
        ResourceKey<Level> level = ResourceKey.create(Registries.DIMENSION, buf.readResourceLocation());
        long position = buf.readVarLong();
        return new ShelfKey(level, position);
    }
}
