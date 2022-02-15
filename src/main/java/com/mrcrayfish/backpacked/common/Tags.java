package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

/**
 * Author: MrCrayfish
 */
public class Tags
{
    public static class Blocks
    {
        public static final IOptionalNamedTag<Block> FUNNELLING = tag("funnelling");

        private static IOptionalNamedTag<Block> tag(String name)
        {
            return BlockTags.createOptional(new ResourceLocation(Reference.MOD_ID, name));
        }
    }
}
