package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.codec.StreamCodec;

public record GiantAugment() implements Augment<GiantAugment>
{
    public static final GiantAugment INSTANCE = new GiantAugment();
    public static final AugmentType<GiantAugment> TYPE = new AugmentType<>(
        Utils.rl("giant"),
        MapCodec.unit(INSTANCE),
        StreamCodec.unit(INSTANCE),
        () -> INSTANCE
    );

    @Override
    public AugmentType<GiantAugment> type()
    {
        return TYPE;
    }
}
