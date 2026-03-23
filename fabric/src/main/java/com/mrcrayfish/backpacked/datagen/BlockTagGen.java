package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.core.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class BlockTagGen extends FabricTagProvider.BlockTagProvider
{
    public BlockTagGen(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        this.getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE).add(
            ModBlocks.OAK_BACKPACK_SHELF.get(),
            ModBlocks.SPRUCE_BACKPACK_SHELF.get(),
            ModBlocks.BIRCH_BACKPACK_SHELF.get(),
            ModBlocks.JUNGLE_BACKPACK_SHELF.get(),
            ModBlocks.DARK_OAK_BACKPACK_SHELF.get(),
            ModBlocks.ACACIA_BACKPACK_SHELF.get(),
            ModBlocks.CRIMSON_BACKPACK_SHELF.get(),
            ModBlocks.WARPED_BACKPACK_SHELF.get()
        );
    }
}
