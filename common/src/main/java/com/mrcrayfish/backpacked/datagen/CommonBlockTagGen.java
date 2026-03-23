package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.core.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class CommonBlockTagGen extends IntrinsicHolderTagsProvider<Block>
{
    public CommonBlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture)
    {
        super(output, Registries.BLOCK, completableFuture, block -> block.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(
            ModBlocks.OAK_BACKPACK_SHELF.get(),
            ModBlocks.SPRUCE_BACKPACK_SHELF.get(),
            ModBlocks.BIRCH_BACKPACK_SHELF.get(),
            ModBlocks.JUNGLE_BACKPACK_SHELF.get(),
            ModBlocks.DARK_OAK_BACKPACK_SHELF.get(),
            ModBlocks.ACACIA_BACKPACK_SHELF.get(),
            ModBlocks.CRIMSON_BACKPACK_SHELF.get(),
            ModBlocks.WARPED_BACKPACK_SHELF.get(),
            ModBlocks.CHERRY_BACKPACK_SHELF.get()
        );
    }
}
