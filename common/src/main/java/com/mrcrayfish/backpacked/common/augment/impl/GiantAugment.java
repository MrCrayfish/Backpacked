package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record GiantAugment(double size) implements Augment<GiantAugment>
{
    public static final AugmentType<GiantAugment> TYPE = new AugmentType<>(
        Utils.rl("giant"),
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("size").forGetter(GiantAugment::size)
        ).apply(instance, GiantAugment::new)),
        StreamCodec.composite(
            ByteBufCodecs.DOUBLE, GiantAugment::size,
            GiantAugment::new
        ),
        () -> new GiantAugment(2)
    );

    @Override
    public AugmentType<GiantAugment> type()
    {
        return TYPE;
    }

    @Override
    public boolean hasSettings()
    {
        return true;
    }

    public GiantAugment setSize(double newSize)
    {
        return new GiantAugment(newSize);
    }
}
