package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;

public record GiantAugment() implements Augment<GiantAugment>
{
    public static final GiantAugment INSTANCE = new GiantAugment();
    public static final AugmentType<GiantAugment> TYPE = new AugmentType<>(
        Utils.rl("giant"),
        Codec.unit(INSTANCE),
        Augment.noOp(),
        Augment.unit(INSTANCE),
        () -> INSTANCE
    );

    @Override
    public AugmentType<GiantAugment> type()
    {
        return TYPE;
    }
}
