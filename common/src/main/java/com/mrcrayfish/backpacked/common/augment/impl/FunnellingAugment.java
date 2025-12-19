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
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public record FunnellingAugment(ItemCollection filters, Mode mode) implements Augment<FunnellingAugment>, FilterableItems<FunnellingAugment>
{
    public static final AugmentType<FunnellingAugment> TYPE = new AugmentType<>(
        Utils.rl("funnelling"),
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemCollection.CODEC.fieldOf("filters").orElse(ItemCollection.EMPTY).forGetter(FunnellingAugment::filters),
            Mode.CODEC.fieldOf("mode").orElse(Mode.ALLOW).forGetter(FunnellingAugment::mode)
        ).apply(instance, FunnellingAugment::new)),
        StreamCodec.composite(
            ItemCollection.STREAM_CODEC,
            FunnellingAugment::filters,
            Mode.STREAM_CODEC,
            FunnellingAugment::mode,
            FunnellingAugment::new
        ),
        () -> new FunnellingAugment(ItemCollection.EMPTY, Mode.ALLOW)
    );

    public FunnellingAugment
    {
        filters = filters.limit(Config.AUGMENTS.funnelling.maxFilters.get());
    }

    @Override
    public AugmentType<FunnellingAugment> type()
    {
        return TYPE;
    }

    @Override
    public FunnellingAugment addItemFilter(Item item)
    {
        return new FunnellingAugment(this.filters.add(item), this.mode);
    }

    @Override
    public FunnellingAugment removeItemFilter(Item item)
    {
        return new FunnellingAugment(this.filters.remove(item), this.mode);
    }

    @Override
    public boolean isFilteringItem(Item item)
    {
        return this.filters.has(item);
    }

    @Override
    public boolean isFilterFull()
    {
        return this.filters.ids().size() >= Config.AUGMENTS.funnelling.maxFilters.get();
    }

    public FunnellingAugment setMode(Mode mode)
    {
        return new FunnellingAugment(this.filters, mode);
    }

    public boolean test(ItemStack stack)
    {
        boolean matched = this.filters.has(stack.getItem());
        if(matched && this.mode == Mode.ALLOW)
            return true;
        if(matched && this.mode == Mode.DISALLOW)
            return false;
        return this.mode == Mode.DISALLOW;
    }

    public enum Mode implements StringRepresentable, LabelAndDescription
    {
        ALLOW, DISALLOW;

        public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
        public static final StreamCodec<FriendlyByteBuf, Mode> STREAM_CODEC = StreamCodec.of(FriendlyByteBuf::writeEnum, buf -> buf.readEnum(Mode.class));
        private static final String LANGUAGE_KEY = "augment.backpacked.funnelling.mode";

        private final Component name;
        private final Component tooltip;

        Mode()
        {
            this.name = Component.translatable(LANGUAGE_KEY, Component.translatable("%s.%s".formatted(LANGUAGE_KEY, this.getSerializedName())));
            this.tooltip = Component.translatable("%s.%s.tooltip".formatted(LANGUAGE_KEY, this.getSerializedName()));
        }

        @Override
        public String getSerializedName()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public Component label()
        {
            return this.name;
        }

        public Component description()
        {
            return this.tooltip;
        }

        public Mode other()
        {
            return switch(this) {
                case ALLOW -> DISALLOW;
                case DISALLOW -> ALLOW;
            };
        }
    }
}
