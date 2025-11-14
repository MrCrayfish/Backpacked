package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.FilterableItems;
import com.mrcrayfish.backpacked.common.ItemCollection;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

public record HopperBridgeAugment(boolean insert, boolean extract, ItemCollection filters) implements Augment<HopperBridgeAugment>, FilterableItems<HopperBridgeAugment>
{
    public static final AugmentType<HopperBridgeAugment> TYPE = new AugmentType<>(
        Utils.rl("hopper_bridge"),
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("insert").orElse(true).forGetter(HopperBridgeAugment::insert),
            Codec.BOOL.fieldOf("extract").orElse(true).forGetter(HopperBridgeAugment::extract),
            ItemCollection.CODEC.fieldOf("filters").orElse(ItemCollection.EMPTY).forGetter(HopperBridgeAugment::filters)
        ).apply(instance, HopperBridgeAugment::new)),
        StreamCodec.composite(
            ByteBufCodecs.BOOL, HopperBridgeAugment::insert,
            ByteBufCodecs.BOOL, HopperBridgeAugment::extract,
            ItemCollection.STREAM_CODEC, HopperBridgeAugment::filters,
            HopperBridgeAugment::new
        ),
        () -> new HopperBridgeAugment(true, true, ItemCollection.EMPTY)
    );

    public HopperBridgeAugment
    {
        filters = filters.limit(128);
    }

    @Override
    public AugmentType<HopperBridgeAugment> type()
    {
        return TYPE;
    }

    public HopperBridgeAugment setInsert(boolean insert)
    {
        return new HopperBridgeAugment(insert, this.extract, this.filters);
    }

    public HopperBridgeAugment setExtract(boolean extract)
    {
        return new HopperBridgeAugment(this.insert, extract, this.filters);
    }

    @Override
    public HopperBridgeAugment addItemFilter(Item item)
    {
        return new HopperBridgeAugment(this.insert, this.extract, this.filters.add(item));
    }

    @Override
    public HopperBridgeAugment removeItemFilter(Item item)
    {
        return new HopperBridgeAugment(this.insert, this.extract, this.filters.remove(item));
    }

    @Override
    public boolean isFilteringItem(Item item)
    {
        return this.filters.has(item);
    }

    @Override
    public boolean isFilterFull()
    {
        return this.filters.ids().size() >= 64;
    }
}
