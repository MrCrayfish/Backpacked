package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.core.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

// IntrinsicHolderTagsProvider was removed in MC 26.2; use TagsProvider directly and pass
// resource keys (via builtInRegistryHolder().key()) instead of raw Block instances.
public class CommonBlockTagGen extends TagsProvider<Block>
{
    public CommonBlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> completableFuture)
    {
        super(output, Registries.BLOCK, completableFuture);
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
