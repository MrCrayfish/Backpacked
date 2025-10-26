package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

public record LightweaverAugment(int minimumLight) implements Augment<LightweaverAugment>
{
    public static final AugmentType<LightweaverAugment> TYPE = new AugmentType<>(
        Utils.rl("lightweaver"),
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("minimum_light").forGetter(LightweaverAugment::minimumLight)
        ).apply(instance, LightweaverAugment::new)),
        StreamCodec.composite(
            ByteBufCodecs.INT, LightweaverAugment::minimumLight,
            LightweaverAugment::new
        ),
        () -> new LightweaverAugment(8)
    );

    public LightweaverAugment(int minimumLight)
    {
        this.minimumLight = Mth.clamp(minimumLight, 0, 15);
    }

    @Override
    public AugmentType<LightweaverAugment> type()
    {
        return TYPE;
    }

    public LightweaverAugment setMinimumLight(int minimumLight)
    {
        return new LightweaverAugment(minimumLight);
    }
}
