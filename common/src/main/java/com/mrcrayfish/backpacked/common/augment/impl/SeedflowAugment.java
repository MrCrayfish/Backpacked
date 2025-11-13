package com.mrcrayfish.backpacked.common.augment.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.FilterableItems;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import com.mrcrayfish.backpacked.common.ItemCollection;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.function.Predicate;

public record SeedflowAugment(boolean randomizeSeeds, boolean useFilters, ItemCollection filters) implements Augment<SeedflowAugment>, FilterableItems<SeedflowAugment>
{
    public static final AugmentType<SeedflowAugment> TYPE = new AugmentType<>(
        Utils.rl("seedflow"),
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("randomize_seeds").orElse(true).forGetter(SeedflowAugment::randomizeSeeds),
            Codec.BOOL.fieldOf("use_filters").orElse(true).forGetter(SeedflowAugment::useFilters),
            ItemCollection.CODEC.fieldOf("filters").orElse(ItemCollection.EMPTY).forGetter(SeedflowAugment::filters)
        ).apply(instance, SeedflowAugment::new)),
        StreamCodec.composite(
            ByteBufCodecs.BOOL, SeedflowAugment::randomizeSeeds,
            ByteBufCodecs.BOOL, SeedflowAugment::useFilters,
            ItemCollection.STREAM_CODEC, SeedflowAugment::filters,
            SeedflowAugment::new
        ),
        () -> new SeedflowAugment(false, true, ItemCollection.EMPTY)
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

    public SeedflowAugment(boolean randomizeSeeds, boolean useFilters, ItemCollection filters)
    {
        this.randomizeSeeds = randomizeSeeds;
        this.useFilters = useFilters;
        this.filters = filters.filter(ITEM_PLACES_AGEABLE_CROP);
    }

    @Override
    public AugmentType<SeedflowAugment> type()
    {
        return TYPE;
    }

    public SeedflowAugment setRandomizeSeeds(boolean randomizeSeeds)
    {
        return new SeedflowAugment(randomizeSeeds, this.useFilters, this.filters);
    }

    public SeedflowAugment setUseFilters(boolean useFilters)
    {
        return new SeedflowAugment(this.randomizeSeeds, useFilters, this.filters);
    }

    @Override
    public SeedflowAugment addItemFilter(Item item)
    {
        return new SeedflowAugment(this.randomizeSeeds, this.useFilters, this.filters.add(item));
    }

    @Override
    public SeedflowAugment removeItemFilter(Item item)
    {
        return new SeedflowAugment(this.randomizeSeeds, this.useFilters, this.filters.remove(item));
    }

    @Override
    public boolean isFilteringItem(Item item)
    {
        return this.filters.has(item);
    }

    public boolean isFilterFull()
    {
        return false;
    }

    private static boolean isAgeableCrop(Block block)
    {
        // Use BushBlock to include nether warts
        if(block instanceof BushBlock && !(block instanceof SaplingBlock))
        {
            // If a crop, we know it has an age property
            if(block instanceof CropBlock)
            {
                return true;
            }

            // Check if the block state has a vanilla age property
            BlockState cropState = block.defaultBlockState();
            for(IntegerProperty property : AGE_PROPERTIES)
            {
                if(cropState.hasProperty(property))
                {
                    return true;
                }
            }

            // Should make compatible with farmers delight
            return cropState.getProperties().stream().anyMatch(p -> {
                return p instanceof IntegerProperty && p.getName().equals("age");
            });
        }
        return false;
    }
}
