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

public record LightweaverAugment(int minimumLight, boolean sound) implements Augment<LightweaverAugment>
{
    public static final AugmentType<LightweaverAugment> TYPE = new AugmentType<>(
        Utils.rl("lightweaver"),
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("minimum_light").orElse(6).forGetter(LightweaverAugment::minimumLight),
            Codec.BOOL.fieldOf("sound").orElse(true).forGetter(LightweaverAugment::sound)
        ).apply(instance, LightweaverAugment::new)),
        StreamCodec.composite(
            ByteBufCodecs.INT, LightweaverAugment::minimumLight,
            ByteBufCodecs.BOOL, LightweaverAugment::sound,
            LightweaverAugment::new
        ),
        () -> new LightweaverAugment(6, true)
    );

    public LightweaverAugment(int minimumLight, boolean sound)
    {
        this.minimumLight = Mth.clamp(minimumLight, 0, 15);
        this.sound = sound;
    }

    @Override
    public AugmentType<LightweaverAugment> type()
    {
        return TYPE;
    }

    public LightweaverAugment setMinimumLight(int minimumLight)
    {
        return new LightweaverAugment(minimumLight, this.sound);
    }

    public LightweaverAugment setSound(boolean sound)
    {
        return new LightweaverAugment(this.minimumLight, sound);
    }
}
