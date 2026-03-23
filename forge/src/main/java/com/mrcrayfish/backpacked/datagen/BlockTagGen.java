package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.core.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagGen extends BlockTagsProvider
{
    public BlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper helper)
    {
        super(output, lookupProvider, Constants.MOD_ID, helper);
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
            ModBlocks.BACKPACK_DOCK.get()
        );
    }
}
