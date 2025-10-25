package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LootboundAugment(boolean blocks, boolean mobs) implements Augment<LootboundAugment>
{
    public static final AugmentType<LootboundAugment> TYPE = new AugmentType<>(
        Utils.rl("lootbound"),
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("blocks").orElse(true).forGetter(LootboundAugment::blocks),
            Codec.BOOL.fieldOf("mobs").orElse(true).forGetter(LootboundAugment::mobs)
        ).apply(instance, LootboundAugment::new)),
        StreamCodec.composite(
            ByteBufCodecs.BOOL, LootboundAugment::blocks,
            ByteBufCodecs.BOOL, LootboundAugment::mobs,
            LootboundAugment::new
        ),
        () -> new LootboundAugment(true, true),
        ModAugmentTypes.FUNNELLING::get
    );

    @Override
    public AugmentType<LootboundAugment> type()
    {
        return TYPE;
    }

    public LootboundAugment setBlocks(boolean blocks)
    {
        return new LootboundAugment(blocks, this.mobs);
    }

    public LootboundAugment setMobs(boolean mobs)
    {
        return new LootboundAugment(this.blocks, mobs);
    }
}
