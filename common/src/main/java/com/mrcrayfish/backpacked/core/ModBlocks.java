package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.block.BackpackDockBlock;
import com.mrcrayfish.backpacked.block.ShelfBlock;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public final class ModBlocks
{
    public static final RegistryEntry<Block> OAK_BACKPACK_SHELF = RegistryEntry.blockWithItem(Utils.id("oak_backpack_shelf"), ShelfBlock::new, () -> Block.Properties.ofFullCopy(Blocks.OAK_PLANKS));
    public static final RegistryEntry<Block> SPRUCE_BACKPACK_SHELF = RegistryEntry.blockWithItem(Utils.id("spruce_backpack_shelf"), ShelfBlock::new, () -> Block.Properties.ofFullCopy(Blocks.SPRUCE_PLANKS));
    public static final RegistryEntry<Block> BIRCH_BACKPACK_SHELF = RegistryEntry.blockWithItem(Utils.id("birch_backpack_shelf"), ShelfBlock::new, () -> Block.Properties.ofFullCopy(Blocks.BIRCH_PLANKS));
    public static final RegistryEntry<Block> JUNGLE_BACKPACK_SHELF = RegistryEntry.blockWithItem(Utils.id("jungle_backpack_shelf"), ShelfBlock::new, () -> Block.Properties.ofFullCopy(Blocks.JUNGLE_PLANKS));
    public static final RegistryEntry<Block> DARK_OAK_BACKPACK_SHELF = RegistryEntry.blockWithItem(Utils.id("dark_oak_backpack_shelf"), ShelfBlock::new, () -> Block.Properties.ofFullCopy(Blocks.DARK_OAK_PLANKS));
    public static final RegistryEntry<Block> ACACIA_BACKPACK_SHELF = RegistryEntry.blockWithItem(Utils.id("acacia_backpack_shelf"), ShelfBlock::new, () -> Block.Properties.ofFullCopy(Blocks.ACACIA_PLANKS));
    public static final RegistryEntry<Block> CRIMSON_BACKPACK_SHELF = RegistryEntry.blockWithItem(Utils.id("crimson_backpack_shelf"), ShelfBlock::new, () -> Block.Properties.ofFullCopy(Blocks.CRIMSON_PLANKS));
    public static final RegistryEntry<Block> WARPED_BACKPACK_SHELF = RegistryEntry.blockWithItem(Utils.id("warped_backpack_shelf"), ShelfBlock::new, () -> Block.Properties.ofFullCopy(Blocks.WARPED_PLANKS));
    public static final RegistryEntry<Block> CHERRY_BACKPACK_SHELF = RegistryEntry.blockWithItem(Utils.id("cherry_backpack_shelf"), ShelfBlock::new, () -> Block.Properties.ofFullCopy(Blocks.CHERRY_PLANKS));
    public static final RegistryEntry<Block> PALE_OAK_BACKPACK_SHELF = RegistryEntry.blockWithItem(Utils.id("pale_oak_backpack_shelf"), ShelfBlock::new, () -> Block.Properties.ofFullCopy(Blocks.PALE_OAK_PLANKS));
    public static final RegistryEntry<Block> BACKPACK_DOCK = RegistryEntry.blockWithItem(Utils.id("backpack_dock"), BackpackDockBlock::new, () -> Block.Properties.ofFullCopy(Blocks.OAK_PLANKS));
}
