package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import com.mrcrayfish.framework.api.registry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Author: MrCrayfish
 */
@RegistryContainer
public class ModBlockEntities
{
    public static final RegistryEntry<BlockEntityType<ShelfBlockEntity>> SHELF = RegistryEntry.blockEntity(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "shelf"), Services.BACKPACK::createShelfBlockEntityType, () -> new Block[]{ModBlocks.OAK_BACKPACK_SHELF.get(), ModBlocks.SPRUCE_BACKPACK_SHELF.get(), ModBlocks.BIRCH_BACKPACK_SHELF.get(), ModBlocks.JUNGLE_BACKPACK_SHELF.get(), ModBlocks.DARK_OAK_BACKPACK_SHELF.get(), ModBlocks.ACACIA_BACKPACK_SHELF.get(), ModBlocks.CRIMSON_BACKPACK_SHELF.get(), ModBlocks.WARPED_BACKPACK_SHELF.get(), ModBlocks.CHERRY_BACKPACK_SHELF.get()});
}
