package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.world.item.Item;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModItems
{
    public static final RegistryEntry<Item> BACKPACK = RegistryEntry.item(Utils.rl("backpack"), () -> Services.BACKPACK.createBackpackItem(new Item.Properties().stacksTo(1)));
    public static final RegistryEntry<Item> UNLOCK_TOKEN = RegistryEntry.item(Utils.rl("unlock_token"), () -> new Item(new Item.Properties()));
}
