package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.codec.StreamCodec;

public record ImmortalAugment() implements Augment<ImmortalAugment>
{
    public static final ImmortalAugment INSTANCE = new ImmortalAugment();
    public static final AugmentType<ImmortalAugment> TYPE = new AugmentType<>(
        Utils.rl("immortal"),
        MapCodec.unit(INSTANCE),
        StreamCodec.unit(INSTANCE),
        () -> INSTANCE
    );

    @Override
    public AugmentType<ImmortalAugment> type()
    {
        return TYPE;
    }
}
