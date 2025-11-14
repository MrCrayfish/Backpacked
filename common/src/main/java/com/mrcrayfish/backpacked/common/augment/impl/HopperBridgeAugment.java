package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.LabelAndDescription;
import com.mrcrayfish.backpacked.common.FilterableItems;
import com.mrcrayfish.backpacked.common.ItemCollection;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

import java.util.Locale;

public record HopperBridgeAugment(boolean insert, boolean extract, FilterMode filterMode, ItemCollection filters) implements Augment<HopperBridgeAugment>, FilterableItems<HopperBridgeAugment>
{
    public static final AugmentType<HopperBridgeAugment> TYPE = new AugmentType<>(
        Utils.rl("hopper_bridge"),
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("insert").orElse(true).forGetter(HopperBridgeAugment::insert),
            Codec.BOOL.fieldOf("extract").orElse(true).forGetter(HopperBridgeAugment::extract),
            FilterMode.CODEC.fieldOf("filter_mode").orElse(FilterMode.OFF).forGetter(HopperBridgeAugment::filterMode),
            ItemCollection.CODEC.fieldOf("filters").orElse(ItemCollection.EMPTY).forGetter(HopperBridgeAugment::filters)
        ).apply(instance, HopperBridgeAugment::new)),
        StreamCodec.composite(
            ByteBufCodecs.BOOL, HopperBridgeAugment::insert,
            ByteBufCodecs.BOOL, HopperBridgeAugment::extract,
            FilterMode.STREAM_CODEC, HopperBridgeAugment::filterMode,
            ItemCollection.STREAM_CODEC, HopperBridgeAugment::filters,
            HopperBridgeAugment::new
        ),
        () -> new HopperBridgeAugment(true, true, FilterMode.OFF, ItemCollection.EMPTY)
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
        return new HopperBridgeAugment(insert, this.extract, this.filterMode, this.filters);
    }

    public HopperBridgeAugment setExtract(boolean extract)
    {
        return new HopperBridgeAugment(this.insert, extract, this.filterMode, this.filters);
    }

    public HopperBridgeAugment setFilterMode(FilterMode mode)
    {
        return new HopperBridgeAugment(this.insert, this.extract, mode, this.filters);
    }

    @Override

    public HopperBridgeAugment addItemFilter(Item item)
    {
        return new HopperBridgeAugment(this.insert, this.extract, this.filterMode, this.filters.add(item));
    }

    @Override
    public HopperBridgeAugment removeItemFilter(Item item)
    {
        return new HopperBridgeAugment(this.insert, this.extract, this.filterMode, this.filters.remove(item));
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

    public enum FilterMode implements StringRepresentable, LabelAndDescription
    {
        OFF(false, false),
        BOTH(true, true),
        INSERT(true, false),
        EXTRACT(false, true);

        public static final Codec<FilterMode> CODEC = StringRepresentable.fromEnum(FilterMode::values);
        public static final StreamCodec<FriendlyByteBuf, FilterMode> STREAM_CODEC = StreamCodec.of(FriendlyByteBuf::writeEnum, buf -> buf.readEnum(FilterMode.class));
        private static final String LANGUAGE_KEY = "augment.backpacked.hopper_bridge.filter_mode";

        private final boolean insert;
        private final boolean extract;
        private final Component name;
        private final Component tooltip;

        FilterMode(boolean insert, boolean extract)
        {
            this.insert = insert;
            this.extract = extract;
            this.name = Component.translatable("%s.%s".formatted(LANGUAGE_KEY, this.getSerializedName()));
            this.tooltip = Component.translatable("%s.%s.tooltip".formatted(LANGUAGE_KEY, this.getSerializedName()));
        }

        @Override
        public String getSerializedName()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public boolean checkInsert()
        {
            return this.insert;
        }

        public boolean checkExtract()
        {
            return this.extract;
        }

        @Override
        public Component label()
        {
            return this.name;
        }

        @Override
        public Component description()
        {
            return this.tooltip;
        }
    }
}
