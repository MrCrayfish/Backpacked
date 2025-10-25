package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.codec.StreamCodec;

public class ReforgeAugment implements Augment<ReforgeAugment>
{
    public static final ReforgeAugment INSTANCE = new ReforgeAugment();
    public static final AugmentType<ReforgeAugment> TYPE = new AugmentType<>(
        Utils.rl("reforge"),
        MapCodec.unit(INSTANCE),
        StreamCodec.unit(INSTANCE),
        () -> INSTANCE
    );

    @Override
    public AugmentType<ReforgeAugment> type()
    {
        return TYPE;
    }
}
