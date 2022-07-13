package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.tileentity.ShelfBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ModBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Reference.MOD_ID);

    public static final RegistryObject<BlockEntityType<ShelfBlockEntity>> SHELF = register("shelf", ShelfBlockEntity::new, () -> new Block[]{ModBlocks.OAK_BACKPACK_SHELF.get(), ModBlocks.SPRUCE_BACKPACK_SHELF.get(), ModBlocks.BIRCH_BACKPACK_SHELF.get(), ModBlocks.JUNGLE_BACKPACK_SHELF.get(), ModBlocks.DARK_OAK_BACKPACK_SHELF.get(), ModBlocks.ACACIA_BACKPACK_SHELF.get(), ModBlocks.CRIMSON_BACKPACK_SHELF.get(), ModBlocks.WARPED_BACKPACK_SHELF.get()});

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String id, BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<Block[]> validBlocksSupplier)
    {
        return REGISTER.register(id, () -> BlockEntityType.Builder.of(supplier, validBlocksSupplier.get()).build(null));
    }
}
