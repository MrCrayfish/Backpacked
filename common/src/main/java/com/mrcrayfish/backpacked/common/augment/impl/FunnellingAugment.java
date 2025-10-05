package com.mrcrayfish.backpacked.common.augment.impl;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class FunnellingAugment implements Augment<FunnellingAugment>
{
    public static final AugmentType<FunnellingAugment> TYPE = new AugmentType<>(
            Utils.rl("funnelling"),
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemFilter.CODEC.listOf().fieldOf("filters").orElse(List.of()).forGetter(FunnellingAugment::filters),
                Mode.CODEC.fieldOf("mode").orElse(Mode.ALLOW).forGetter(FunnellingAugment::mode)
            ).apply(instance, FunnellingAugment::new)),
            StreamCodec.composite(
                ItemFilter.STREAM_CODEC.apply(ByteBufCodecs.list()),
                FunnellingAugment::filters,
                Mode.STREAM_CODEC,
                FunnellingAugment::mode,
                FunnellingAugment::new
            ),
            () -> new FunnellingAugment(List.of(), Mode.ALLOW)
    );
    private static final ResourceLocation AIR = ResourceLocation.withDefaultNamespace("air");
    private final List<ItemFilter> filters;
    private final Mode mode;
    private @Nullable Map<Item, List<ItemFilter>> lookup;

    public FunnellingAugment(List<ItemFilter> filters, Mode mode)
    {
        this.filters = filters.stream().filter(filter -> {
            return BuiltInRegistries.ITEM.containsKey(filter.id) && !filter.id.equals(AIR);
        }).collect(Collectors.toList());
        this.mode = mode;
    }

    @Override
    public AugmentType<FunnellingAugment> type()
    {
        return TYPE;
    }

    public FunnellingAugment addFilter(ResourceLocation id)
    {
        List<ItemFilter> filters = new ArrayList<>(this.filters);
        filters.add(new ItemFilter(id));
        return new FunnellingAugment(filters, this.mode);
    }

    public FunnellingAugment removeFilter(ResourceLocation id)
    {
        List<ItemFilter> filters = new ArrayList<>(this.filters);
        filters.removeIf(filter -> filter.id.equals(id));
        return new FunnellingAugment(filters, this.mode);
    }

    public boolean isFilter(Item item)
    {
        Map<Item, List<ItemFilter>> lookup = this.buildLookup();
        return lookup.containsKey(item);
    }

    public List<ItemFilter> filters()
    {
        return this.filters;
    }

    public Mode mode()
    {
        return this.mode;
    }

    public boolean test(ItemStack stack)
    {
        Map<Item, List<ItemFilter>> lookup = this.buildLookup();
        boolean matched = this.testFilters(stack, lookup.getOrDefault(stack.getItem(), Collections.emptyList()));
        return (this.mode == Mode.ALLOW) == matched;
    }

    private boolean testFilters(ItemStack stack, List<ItemFilter> filters)
    {
        for(ItemFilter filter : filters)
        {
            if(filter.match(stack))
            {
                return true;
            }
        }
        return false;
    }

    private Map<Item, List<ItemFilter>> buildLookup()
    {
        if(this.lookup == null)
        {
            Map<Item, List<ItemFilter>> map = new HashMap<>();
            this.filters.forEach(filter -> map.computeIfAbsent(filter.item(), k -> new ArrayList<>()).add(filter));
            ImmutableMap.Builder<Item, List<ItemFilter>> builder = ImmutableMap.builder();
            map.forEach((item, filters) -> builder.put(item, Collections.unmodifiableList(filters)));
            this.lookup = builder.build();
        }
        return this.lookup;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == this) return true;
        if(obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FunnellingAugment) obj;
        return Objects.equals(this.filters, that.filters) && Objects.equals(this.mode, that.mode);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.filters, this.mode);
    }

    public static final class ItemFilter
    {
        private static final Codec<ItemFilter> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                ResourceLocation.CODEC.fieldOf("item").forGetter(ItemFilter::id)
        ).apply(instance, ItemFilter::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, ItemFilter> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, ItemFilter::id,
                ItemFilter::new
        );

        private final ResourceLocation id;
        private Item item;

        public ItemFilter(ResourceLocation id)
        {
            this.id = id;
        }

        public ResourceLocation id()
        {
            return this.id;
        }

        public Item item()
        {
            if(this.item == null)
            {
                this.item = BuiltInRegistries.ITEM.get(this.id);
            }
            return this.item;
        }

        @Override
        public boolean equals(Object obj)
        {
            if(obj == this) return true;
            if(obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ItemFilter) obj;
            return Objects.equals(this.id, that.id);
        }

        @Override
        public int hashCode()
        {
            return this.id.hashCode();
        }

        public boolean match(ItemStack stack)
        {
            return stack.is(this.item());
        }
    }

    public enum Mode implements StringRepresentable
    {
        ALLOW, DISALLOW;

        public static final Codec<Mode> CODEC = StringRepresentable.fromEnum(Mode::values);
        public static final StreamCodec<FriendlyByteBuf, Mode> STREAM_CODEC = StreamCodec.of(FriendlyByteBuf::writeEnum, buf -> buf.readEnum(Mode.class));

        @Override
        public String getSerializedName()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
