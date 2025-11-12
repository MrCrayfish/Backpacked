package com.mrcrayfish.backpacked.common.augment.impl;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// TODO split into two different augments (one for replanting, and one for seeding)
public final class SeedflowAugment implements Augment<SeedflowAugment>
{
    public static final AugmentType<SeedflowAugment> TYPE = new AugmentType<>(
        Utils.rl("seedflow"),
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("plant_nearby").orElse(true).forGetter(SeedflowAugment::plantNearby),
            Codec.BOOL.fieldOf("randomize_seeds").orElse(true).forGetter(SeedflowAugment::randomizeSeeds),
            Codec.BOOL.fieldOf("use_filters").orElse(true).forGetter(SeedflowAugment::useFilters),
            ItemFilter.CODEC.sizeLimitedListOf(64).fieldOf("filters").orElse(List.of()).forGetter(SeedflowAugment::filters)
        ).apply(instance, SeedflowAugment::new)),
        StreamCodec.composite(
            ByteBufCodecs.BOOL, SeedflowAugment::plantNearby,
            ByteBufCodecs.BOOL, SeedflowAugment::randomizeSeeds,
            ByteBufCodecs.BOOL, SeedflowAugment::useFilters,
            ItemFilter.STREAM_CODEC.apply(ByteBufCodecs.list()), SeedflowAugment::filters,
            SeedflowAugment::new
        ),
        () -> new SeedflowAugment(true, false, true, List.of())
    );
    public static final Predicate<Item> ITEM_PLACES_AGEABLE_CROP = item -> {
        return item instanceof BlockItem blockItem && isAgeableCrop(blockItem.getBlock());
    };
    public static final IntegerProperty[] AGE_PROPERTIES = {
        BlockStateProperties.AGE_1,
        BlockStateProperties.AGE_2,
        BlockStateProperties.AGE_3,
        BlockStateProperties.AGE_4,
        BlockStateProperties.AGE_5,
        BlockStateProperties.AGE_7,
        BlockStateProperties.AGE_15,
        BlockStateProperties.AGE_25
    };

    private final boolean plantNearby;
    private final boolean randomizeSeeds;
    private final boolean useFilters;
    private final List<ItemFilter> filters;
    private @Nullable Map<Item, List<ItemFilter>> lookup;

    public SeedflowAugment(boolean plantNearby, boolean randomizeSeeds, boolean useFilters, List<ItemFilter> filters)
    {
        this.plantNearby = plantNearby;
        this.randomizeSeeds = randomizeSeeds;
        this.useFilters = useFilters;
        this.filters = filters.stream().filter(filter -> {
            Item item = filter.item();
            return item != null && ITEM_PLACES_AGEABLE_CROP.test(item);
        }).limit(64).collect(Collectors.toList());
    }

    @Override
    public AugmentType<SeedflowAugment> type()
    {
        return TYPE;
    }

    public SeedflowAugment setPlantNearby(boolean plantNearby)
    {
        return new SeedflowAugment(plantNearby, this.randomizeSeeds, this.useFilters, this.filters);
    }

    public SeedflowAugment setRandomizeSeeds(boolean randomizeSeeds)
    {
        return new SeedflowAugment(this.plantNearby, randomizeSeeds, this.useFilters, this.filters);
    }

    public SeedflowAugment setUseFilters(boolean useFilters)
    {
        return new SeedflowAugment(this.plantNearby, this.randomizeSeeds, useFilters, this.filters);
    }

    public SeedflowAugment addFilter(ResourceLocation id)
    {
        List<ItemFilter> filters = new ArrayList<>(this.filters);
        filters.add(new ItemFilter(id));
        return new SeedflowAugment(this.plantNearby, this.randomizeSeeds, this.useFilters, filters);
    }

    public SeedflowAugment removeFilter(ResourceLocation id)
    {
        List<ItemFilter> filters = new ArrayList<>(this.filters);
        filters.removeIf(filter -> filter.id.equals(id));
        return new SeedflowAugment(this.plantNearby, this.randomizeSeeds, this.useFilters, filters);
    }

    public boolean isFilter(Item item)
    {
         Map<Item, List<ItemFilter>> lookup = this.buildLookup();
        return lookup.containsKey(item);
    }

    public boolean isFilterLimit()
    {
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

    public boolean plantNearby()
    {
        return this.plantNearby;
    }

    public boolean randomizeSeeds()
    {
        return this.randomizeSeeds;
    }

    public boolean useFilters()
    {
        return this.useFilters;
    }

    public List<ItemFilter> filters()
    {
        return this.filters;
    }

    private static boolean isAgeableCrop(Block block)
    {
        // Use BushBlock to include nether warts
        if(block instanceof BushBlock && !(block instanceof SaplingBlock))
        {
            // Check if the crop has an age property
            BlockState cropState = block.defaultBlockState();
            for(IntegerProperty property : AGE_PROPERTIES)
            {
                if(cropState.hasProperty(property))
                {
                    return true;
                }
            }
        }
        return false;
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
}
