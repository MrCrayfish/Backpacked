package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.FriendlyByteBuf;

public record LootboundAugment(boolean blocks, boolean mobs) implements Augment<LootboundAugment>
{
    public static final AugmentType<LootboundAugment> TYPE = new AugmentType<>(
        Utils.rl("lootbound"),
        RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("blocks").orElse(true).forGetter(LootboundAugment::blocks),
            Codec.BOOL.fieldOf("mobs").orElse(true).forGetter(LootboundAugment::mobs)
        ).apply(instance, LootboundAugment::new)),
        LootboundAugment::encode,
        LootboundAugment::decode,
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

    private static void encode(FriendlyByteBuf buf, LootboundAugment augment)
    {
        buf.writeBoolean(augment.blocks);
        buf.writeBoolean(augment.mobs);
    }

    private static LootboundAugment decode(FriendlyByteBuf buf)
    {
        boolean blocks = buf.readBoolean();
        boolean mobs = buf.readBoolean();
        return new LootboundAugment(blocks, mobs);
    }
}
