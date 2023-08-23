package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.common.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tags.BlockTags;

/**
 * Author: MrCrayfish
 */
public class BlockTagGen extends FabricTagProvider.BlockTagProvider
{
    public BlockTagGen(FabricDataGenerator generator)
    {
        super(generator);
    }

    @Override
    protected void generateTags()
    {
        // Does Fabric have any standard tags?
        this.getOrCreateTagBuilder(Tags.Blocks.FUNNELLING)
                .forceAddTag(BlockTags.GOLD_ORES)
                .forceAddTag(BlockTags.IRON_ORES)
                .forceAddTag(BlockTags.DIAMOND_ORES)
                .forceAddTag(BlockTags.REDSTONE_ORES)
                .forceAddTag(BlockTags.LAPIS_ORES)
                .forceAddTag(BlockTags.COAL_ORES)
                .forceAddTag(BlockTags.EMERALD_ORES)
                .forceAddTag(BlockTags.COPPER_ORES);
    }
}
