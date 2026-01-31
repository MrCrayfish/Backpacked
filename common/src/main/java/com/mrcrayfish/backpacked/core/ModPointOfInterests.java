package com.mrcrayfish.backpacked.core;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.backpacked.common.PointOfInterest;
import net.minecraft.world.entity.ai.village.poi.PoiType;

import java.util.stream.Stream;

public class ModPointOfInterests
{
    public static final PointOfInterest BACKPACK_SHELF = new PointOfInterest("backpack_shelf", Suppliers.memoize(() -> {
        var states = Stream.of(ModBlocks.OAK_BACKPACK_SHELF.get(), ModBlocks.SPRUCE_BACKPACK_SHELF.get(), ModBlocks.BIRCH_BACKPACK_SHELF.get(), ModBlocks.JUNGLE_BACKPACK_SHELF.get(), ModBlocks.DARK_OAK_BACKPACK_SHELF.get(), ModBlocks.ACACIA_BACKPACK_SHELF.get(), ModBlocks.CRIMSON_BACKPACK_SHELF.get(), ModBlocks.WARPED_BACKPACK_SHELF.get(), ModBlocks.CHERRY_BACKPACK_SHELF.get())
            .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
            .collect(ImmutableSet.toImmutableSet());
        return new PoiType(states, 16, 1);
    }));
}
