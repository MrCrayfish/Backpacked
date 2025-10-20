package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.codec.StreamCodec;

public class ImbuedHideAugment implements Augment<ImbuedHideAugment>
{
    public static final ImbuedHideAugment INSTANCE = new ImbuedHideAugment();
    public static final AugmentType<ImbuedHideAugment> TYPE = new AugmentType<>(
            Utils.rl("imbued_hide"),
            MapCodec.unit(INSTANCE),
            StreamCodec.unit(INSTANCE),
            () -> INSTANCE
    );

    @Override
    public AugmentType<ImbuedHideAugment> type()
    {
        return TYPE;
    }
}
