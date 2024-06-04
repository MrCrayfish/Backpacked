package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

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
            return TagKey.create(Registries.ITEM, new ResourceLocation(Constants.MOD_ID, name));
        }
    }
}
