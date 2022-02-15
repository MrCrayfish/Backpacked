package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.common.Tags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * Author: MrCrayfish
 */
public class BlockTagGen extends BlockTagsProvider
{
    public BlockTagGen(DataGenerator generator, ExistingFileHelper existingFileHelper)
    {
        super(generator, Reference.MOD_ID, existingFileHelper);
    }

    @Override
    public void addTags()
    {
       this.tag(Tags.Blocks.FUNNELLING)
               .addTag(net.minecraftforge.common.Tags.Blocks.ORES);
    }

    @Override
    public String getName()
    {
        return "Backpacked Block Tags";
    }
}
