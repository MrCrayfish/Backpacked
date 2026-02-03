package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;

public class ReforgeAugment implements Augment<ReforgeAugment>
{
    public static final ReforgeAugment INSTANCE = new ReforgeAugment();
    public static final AugmentType<ReforgeAugment> TYPE = new AugmentType<>(
        Utils.rl("reforge"),
        Codec.unit(INSTANCE),
        Augment.noOp(),
        Augment.unit(INSTANCE),
        () -> INSTANCE
    );

    @Override
    public AugmentType<ReforgeAugment> type()
    {
        return TYPE;
    }
}
