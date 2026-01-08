package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Author: MrCrayfish
 */
public class ModTags
{
    public static class Items
    {
        public static final TagKey<Item> BACKPACK_ENCHANTABLE = create("enchantable/backpack");

        private static TagKey<Item> create(String name)
        {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Blocks
    {
        public static final TagKey<Block> FUNNELLING = create("funnelling");

        private static TagKey<Block> create(String name)
        {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }
}
