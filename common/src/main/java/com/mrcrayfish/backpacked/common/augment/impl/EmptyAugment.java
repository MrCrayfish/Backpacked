package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.codec.StreamCodec;

public record EmptyAugment() implements Augment<EmptyAugment>
{
    public static final EmptyAugment INSTANCE = new EmptyAugment();
    public static final AugmentType<EmptyAugment> TYPE = new AugmentType<>(
        Utils.rl("empty"),
        MapCodec.unit(INSTANCE),
        StreamCodec.unit(INSTANCE),
        () -> INSTANCE
    );

    @Override
    public AugmentType<EmptyAugment> type()
    {
        return TYPE;
    }
}
