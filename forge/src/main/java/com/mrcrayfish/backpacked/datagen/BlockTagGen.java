package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.Tags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
public class BlockTagGen extends BlockTagsProvider
{
    public BlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper)
    {
        super(output, lookupProvider, Constants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        this.tag(Tags.Blocks.FUNNELLING).addTag(net.minecraftforge.common.Tags.Blocks.ORES);
    }

    @Override
    public String getName()
    {
        return "Backpacked Block Tags";
    }
}
