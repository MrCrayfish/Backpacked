package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.core.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

// Fabric's data generator mixes into vanilla TagsProvider and casts the instance to FabricTagsProvider,
// so the shared CommonBlockTagGen (plain TagsProvider) can't be registered directly on Fabric.
public class FabricBlockTagGen extends FabricTagsProvider.BlockTagsProvider
{
    public FabricBlockTagGen(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture)
    {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(
            ModBlocks.OAK_BACKPACK_SHELF.get().builtInRegistryHolder().key(),
            ModBlocks.SPRUCE_BACKPACK_SHELF.get().builtInRegistryHolder().key(),
            ModBlocks.BIRCH_BACKPACK_SHELF.get().builtInRegistryHolder().key(),
            ModBlocks.JUNGLE_BACKPACK_SHELF.get().builtInRegistryHolder().key(),
            ModBlocks.DARK_OAK_BACKPACK_SHELF.get().builtInRegistryHolder().key(),
            ModBlocks.ACACIA_BACKPACK_SHELF.get().builtInRegistryHolder().key(),
            ModBlocks.CRIMSON_BACKPACK_SHELF.get().builtInRegistryHolder().key(),
            ModBlocks.WARPED_BACKPACK_SHELF.get().builtInRegistryHolder().key(),
            ModBlocks.CHERRY_BACKPACK_SHELF.get().builtInRegistryHolder().key(),
            ModBlocks.PALE_OAK_BACKPACK_SHELF.get().builtInRegistryHolder().key(),
            ModBlocks.BACKPACK_DOCK.get().builtInRegistryHolder().key()
        );
    }
}
