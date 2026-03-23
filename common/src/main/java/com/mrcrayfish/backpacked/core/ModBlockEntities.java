package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.blockentity.BackpackDockBlockEntity;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModBlockEntities
{
    public static final RegistryEntry<BlockEntityType<ShelfBlockEntity>> BACKPACK_SHELF = RegistryEntry.blockEntity(
        Utils.id("shelf"),
        Services.BACKPACK::createShelfBlockEntityType,
        () -> new Block[] {
            ModBlocks.OAK_BACKPACK_SHELF.get(),
            ModBlocks.SPRUCE_BACKPACK_SHELF.get(),
            ModBlocks.BIRCH_BACKPACK_SHELF.get(),
            ModBlocks.JUNGLE_BACKPACK_SHELF.get(),
            ModBlocks.DARK_OAK_BACKPACK_SHELF.get(),
            ModBlocks.ACACIA_BACKPACK_SHELF.get(),
            ModBlocks.CRIMSON_BACKPACK_SHELF.get(),
            ModBlocks.WARPED_BACKPACK_SHELF.get(),
            ModBlocks.CHERRY_BACKPACK_SHELF.get(),
            ModBlocks.PALE_OAK_BACKPACK_SHELF.get(),
        });
    public static final RegistryEntry<BlockEntityType<BackpackDockBlockEntity>> BACKPACK_DOCK = RegistryEntry.blockEntity(
        Utils.id("backpack_dock"),
        BackpackDockBlockEntity::new,
        () -> new Block[] {
            ModBlocks.BACKPACK_DOCK.get()
        });
}
