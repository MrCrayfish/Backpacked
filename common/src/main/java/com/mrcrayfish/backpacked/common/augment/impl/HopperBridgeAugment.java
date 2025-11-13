package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record HopperBridgeAugment(boolean insert, boolean extract) implements Augment<HopperBridgeAugment>
{
    public static final AugmentType<HopperBridgeAugment> TYPE = new AugmentType<>(
        Utils.rl("hopper_bridge"),
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("insert").orElse(true).forGetter(HopperBridgeAugment::insert),
            Codec.BOOL.fieldOf("extract").orElse(true).forGetter(HopperBridgeAugment::extract)
        ).apply(instance, HopperBridgeAugment::new)),
        StreamCodec.composite(
            ByteBufCodecs.BOOL, HopperBridgeAugment::insert,
            ByteBufCodecs.BOOL, HopperBridgeAugment::extract,
            HopperBridgeAugment::new
        ),
        () -> new HopperBridgeAugment(true, true)
    );

    @Override
    public AugmentType<HopperBridgeAugment> type()
    {
        return TYPE;
    }

    public HopperBridgeAugment setInsert(boolean insert)
    {
        return new HopperBridgeAugment(insert, this.extract);
    }

    public HopperBridgeAugment setExtract(boolean extract)
    {
        return new HopperBridgeAugment(this.insert, extract);
    }
}
