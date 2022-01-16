package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.tileentity.ShelfTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ModTileEntities
{
    public static final DeferredRegister<TileEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Reference.MOD_ID);

    public static final RegistryObject<TileEntityType<ShelfTileEntity>> SHELF = register("shelf", ShelfTileEntity::new, () -> new Block[]{ModBlocks.OAK_SHELF.get()});

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(String id, Supplier<T> factoryIn, Supplier<Block[]> validBlocksSupplier)
    {
        return REGISTER.register(id, () -> TileEntityType.Builder.of(factoryIn, validBlocksSupplier.get()).build(null));
    }
}
