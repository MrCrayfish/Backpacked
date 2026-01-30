package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
 // TODO DONE
public record ImmortalAugment() implements Augment<ImmortalAugment>
{
    public static final ImmortalAugment INSTANCE = new ImmortalAugment();
    public static final AugmentType<ImmortalAugment> TYPE = new AugmentType<>(
        Utils.rl("immortal"),
        Codec.unit(INSTANCE),
        Augment.noOp(),
        Augment.unit(INSTANCE),
        () -> INSTANCE
    );

    @Override
    public AugmentType<ImmortalAugment> type()
    {
        return TYPE;
    }
}
