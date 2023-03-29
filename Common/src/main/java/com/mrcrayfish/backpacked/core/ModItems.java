package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModItems
{
    public static final RegistryEntry<Item> BACKPACK = RegistryEntry.item(new ResourceLocation(Constants.MOD_ID, "backpack"), () -> Services.BACKPACK.createBackpackItem(new Item.Properties().stacksTo(1).tab(Services.PLATFORM.getCreativeModTab())));
    public static final RegistryEntry<Item> OAK_BACKPACK_SHELF = RegistryEntry.item(new ResourceLocation(Constants.MOD_ID, "oak_backpack_shelf"), () -> new BlockItem(ModBlocks.OAK_BACKPACK_SHELF.get(), new Item.Properties().tab(Services.PLATFORM.getCreativeModTab())));
    public static final RegistryEntry<Item> SPRUCE_BACKPACK_SHELF = RegistryEntry.item(new ResourceLocation(Constants.MOD_ID, "spruce_backpack_shelf"), () -> new BlockItem(ModBlocks.SPRUCE_BACKPACK_SHELF.get(), new Item.Properties().tab(Services.PLATFORM.getCreativeModTab())));
    public static final RegistryEntry<Item> BIRCH_BACKPACK_SHELF = RegistryEntry.item(new ResourceLocation(Constants.MOD_ID, "birch_backpack_shelf"), () -> new BlockItem(ModBlocks.BIRCH_BACKPACK_SHELF.get(), new Item.Properties().tab(Services.PLATFORM.getCreativeModTab())));
    public static final RegistryEntry<Item> JUNGLE_BACKPACK_SHELF = RegistryEntry.item(new ResourceLocation(Constants.MOD_ID, "jungle_backpack_shelf"), () -> new BlockItem(ModBlocks.JUNGLE_BACKPACK_SHELF.get(), new Item.Properties().tab(Services.PLATFORM.getCreativeModTab())));
    public static final RegistryEntry<Item> DARK_OAK_BACKPACK_SHELF = RegistryEntry.item(new ResourceLocation(Constants.MOD_ID, "dark_oak_backpack_shelf"), () -> new BlockItem(ModBlocks.DARK_OAK_BACKPACK_SHELF.get(), new Item.Properties().tab(Services.PLATFORM.getCreativeModTab())));
    public static final RegistryEntry<Item> ACACIA_BACKPACK_SHELF = RegistryEntry.item(new ResourceLocation(Constants.MOD_ID, "acacia_backpack_shelf"), () -> new BlockItem(ModBlocks.ACACIA_BACKPACK_SHELF.get(), new Item.Properties().tab(Services.PLATFORM.getCreativeModTab())));
    public static final RegistryEntry<Item> CRIMSON_BACKPACK_SHELF = RegistryEntry.item(new ResourceLocation(Constants.MOD_ID, "crimson_backpack_shelf"), () -> new BlockItem(ModBlocks.CRIMSON_BACKPACK_SHELF.get(), new Item.Properties().tab(Services.PLATFORM.getCreativeModTab())));
    public static final RegistryEntry<Item> WARPED_BACKPACK_SHELF = RegistryEntry.item(new ResourceLocation(Constants.MOD_ID, "warped_backpack_shelf"), () -> new BlockItem(ModBlocks.WARPED_BACKPACK_SHELF.get(), new Item.Properties().tab(Services.PLATFORM.getCreativeModTab())));
}
