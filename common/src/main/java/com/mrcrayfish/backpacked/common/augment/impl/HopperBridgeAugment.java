package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.client.LabelAndDescription;
import com.mrcrayfish.backpacked.common.FilterableItems;
import com.mrcrayfish.backpacked.common.ItemCollection;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

import java.util.Locale;
 // TODO DONE
public record HopperBridgeAugment(boolean insert, boolean extract, FilterMode filterMode, ItemCollection filters) implements Augment<HopperBridgeAugment>, FilterableItems<HopperBridgeAugment>
{
    public static final AugmentType<HopperBridgeAugment> TYPE = new AugmentType<>(
        Utils.rl("hopper_bridge"),
        RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("insert").orElse(true).forGetter(HopperBridgeAugment::insert),
            Codec.BOOL.fieldOf("extract").orElse(true).forGetter(HopperBridgeAugment::extract),
            FilterMode.CODEC.fieldOf("filter_mode").orElse(FilterMode.OFF).forGetter(HopperBridgeAugment::filterMode),
            ItemCollection.CODEC.fieldOf("filters").orElse(ItemCollection.EMPTY).forGetter(HopperBridgeAugment::filters)
        ).apply(instance, HopperBridgeAugment::new)),
        HopperBridgeAugment::encode,
        HopperBridgeAugment::decode,
        () -> new HopperBridgeAugment(true, true, FilterMode.OFF, ItemCollection.EMPTY)
    );

    public HopperBridgeAugment
    {
        filters = filters.limit(Config.AUGMENTS.hopperBridge.maxFilters.get());
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
        return this.filters.ids().size() >= Config.AUGMENTS.hopperBridge.maxFilters.get();
    }

    private static void encode(FriendlyByteBuf buf, HopperBridgeAugment augment)
    {
        buf.writeBoolean(augment.insert);
        buf.writeBoolean(augment.extract);
        buf.writeEnum(augment.filterMode);
        augment.filters.encode(buf);
    }

    private static HopperBridgeAugment decode(FriendlyByteBuf buf)
    {
        boolean insert = buf.readBoolean();
        boolean extract = buf.readBoolean();
        FilterMode filterMode = buf.readEnum(FilterMode.class);
        ItemCollection filters = ItemCollection.decode(buf);
        return new HopperBridgeAugment(insert, extract, filterMode, filters);
    }

    public enum FilterMode implements StringRepresentable, LabelAndDescription
    {
        OFF(false, false),
        BOTH(true, true),
        INSERT(true, false),
        EXTRACT(false, true);

        public static final Codec<FilterMode> CODEC = StringRepresentable.fromEnum(FilterMode::values);
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
