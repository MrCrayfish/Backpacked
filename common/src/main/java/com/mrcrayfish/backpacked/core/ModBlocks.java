package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.block.ShelfBlock;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public final class ModBlocks
{
    public static final RegistryEntry<Block> OAK_BACKPACK_SHELF = RegistryEntry.blockWithItem(new ResourceLocation(Constants.MOD_ID, "oak_backpack_shelf"), () -> new ShelfBlock(Block.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryEntry<Block> SPRUCE_BACKPACK_SHELF = RegistryEntry.blockWithItem(new ResourceLocation(Constants.MOD_ID, "spruce_backpack_shelf"), () -> new ShelfBlock(Block.Properties.copy(Blocks.SPRUCE_PLANKS)));
    public static final RegistryEntry<Block> BIRCH_BACKPACK_SHELF = RegistryEntry.blockWithItem(new ResourceLocation(Constants.MOD_ID, "birch_backpack_shelf"), () -> new ShelfBlock(Block.Properties.copy(Blocks.BIRCH_PLANKS)));
    public static final RegistryEntry<Block> JUNGLE_BACKPACK_SHELF = RegistryEntry.blockWithItem(new ResourceLocation(Constants.MOD_ID, "jungle_backpack_shelf"), () -> new ShelfBlock(Block.Properties.copy(Blocks.JUNGLE_PLANKS)));
    public static final RegistryEntry<Block> DARK_OAK_BACKPACK_SHELF = RegistryEntry.blockWithItem(new ResourceLocation(Constants.MOD_ID, "dark_oak_backpack_shelf"), () -> new ShelfBlock(Block.Properties.copy(Blocks.DARK_OAK_PLANKS)));
    public static final RegistryEntry<Block> ACACIA_BACKPACK_SHELF = RegistryEntry.blockWithItem(new ResourceLocation(Constants.MOD_ID, "acacia_backpack_shelf"), () -> new ShelfBlock(Block.Properties.copy(Blocks.ACACIA_PLANKS)));
    public static final RegistryEntry<Block> CRIMSON_BACKPACK_SHELF = RegistryEntry.blockWithItem(new ResourceLocation(Constants.MOD_ID, "crimson_backpack_shelf"), () -> new ShelfBlock(Block.Properties.copy(Blocks.CRIMSON_PLANKS)));
    public static final RegistryEntry<Block> WARPED_BACKPACK_SHELF = RegistryEntry.blockWithItem(new ResourceLocation(Constants.MOD_ID, "warped_backpack_shelf"), () -> new ShelfBlock(Block.Properties.copy(Blocks.WARPED_PLANKS)));
    public static final RegistryEntry<Block> CHERRY_BACKPACK_SHELF = RegistryEntry.blockWithItem(new ResourceLocation(Constants.MOD_ID, "cherry_backpack_shelf"), () -> new ShelfBlock(Block.Properties.copy(Blocks.CHERRY_PLANKS)));
}
