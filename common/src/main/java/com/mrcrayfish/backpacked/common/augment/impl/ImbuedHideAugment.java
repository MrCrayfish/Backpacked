package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
 // TODO DONE
public class ImbuedHideAugment implements Augment<ImbuedHideAugment>
{
    public static final ImbuedHideAugment INSTANCE = new ImbuedHideAugment();
    public static final AugmentType<ImbuedHideAugment> TYPE = new AugmentType<>(
        Utils.rl("imbued_hide"),
        Codec.unit(INSTANCE),
        Augment.noOp(),
        Augment.unit(INSTANCE),
        () -> INSTANCE
    );

    @Override
    public AugmentType<ImbuedHideAugment> type()
    {
        return TYPE;
    }
}
