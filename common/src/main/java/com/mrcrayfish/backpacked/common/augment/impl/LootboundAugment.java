package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LootboundAugment(boolean blocks, boolean entities) implements Augment<LootboundAugment>
{
    public static final AugmentType<LootboundAugment> TYPE = new AugmentType<>(
        Utils.rl("lootbound"),
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("blocks").orElse(true).forGetter(LootboundAugment::blocks),
            Codec.BOOL.fieldOf("entities").orElse(true).forGetter(LootboundAugment::entities)
        ).apply(instance, LootboundAugment::new)),
        StreamCodec.composite(
            ByteBufCodecs.BOOL, LootboundAugment::blocks,
            ByteBufCodecs.BOOL, LootboundAugment::entities,
            LootboundAugment::new
        ),
        () -> new LootboundAugment(true, true)
    );

    @Override
    public AugmentType<LootboundAugment> type()
    {
        return TYPE;
    }
}
