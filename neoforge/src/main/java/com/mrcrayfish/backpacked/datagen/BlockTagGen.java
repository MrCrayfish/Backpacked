package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.core.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

import static net.neoforged.neoforge.common.Tags.Blocks.ORES;

/**
 * Author: MrCrayfish
 */
public class BlockTagGen extends BlockTagsProvider
{
    public BlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, lookupProvider, Constants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        this.tag(ModTags.Blocks.FUNNELLING).addTag(ORES);
    }

    @Override
    public String getName()
    {
        return "Backpacked Block Tags";
    }
}
