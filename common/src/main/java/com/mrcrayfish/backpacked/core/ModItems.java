package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModItems
{
    public static final RegistryEntry<Item> BACKPACK = RegistryEntry.item(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack"), () -> Services.BACKPACK.createBackpackItem(new Item.Properties().stacksTo(1)));
}
