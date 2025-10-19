package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.codec.StreamCodec;

public record QuiverlinkAugment() implements Augment<QuiverlinkAugment>
{
    public static final AugmentType<QuiverlinkAugment> TYPE = new AugmentType<>(
        Utils.rl("quiverlink"),
        MapCodec.unit(new QuiverlinkAugment()),
        StreamCodec.unit(new QuiverlinkAugment()),
        QuiverlinkAugment::new
    );

    @Override
    public AugmentType<QuiverlinkAugment> type()
    {
        return TYPE;
    }

    @Override
    public boolean equals(Object obj)
    {
        // Need to do this to satisfy codecs
        return obj != null && obj.getClass() == this.getClass();
    }
}
