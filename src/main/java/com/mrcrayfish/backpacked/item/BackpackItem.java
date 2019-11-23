package com.mrcrayfish.backpacked.item;

import com.mrcrayfish.backpacked.Reference;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class BackpackItem extends Item
{
    public BackpackItem(Properties properties)
    {
        super(properties);
        this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "backpack"));
    }
}
