package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
 // TODO DONE
public final class FarmhandAugment implements Augment<FarmhandAugment>
{
    public static final FarmhandAugment INSTANCE = new FarmhandAugment();
    public static final AugmentType<FarmhandAugment> TYPE = new AugmentType<>(
        Utils.rl("farmhand"),
        Codec.unit(INSTANCE),
        Augment.noOp(),
        Augment.unit(INSTANCE),
        () -> INSTANCE
    );

    @Override
    public AugmentType<FarmhandAugment> type()
    {
        return TYPE;
    }
}
