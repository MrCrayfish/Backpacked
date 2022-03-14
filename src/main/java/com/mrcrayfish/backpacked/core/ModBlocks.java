package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.block.ShelfBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ModBlocks
{
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);

    public static final RegistryObject<Block> OAK_BACKPACK_SHELF = register("oak_backpack_shelf", () -> new ShelfBlock(Block.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> SPRUCE_BACKPACK_SHELF = register("spruce_backpack_shelf", () -> new ShelfBlock(Block.Properties.copy(Blocks.SPRUCE_PLANKS)));
    public static final RegistryObject<Block> BIRCH_BACKPACK_SHELF = register("birch_backpack_shelf", () -> new ShelfBlock(Block.Properties.copy(Blocks.BIRCH_PLANKS)));
    public static final RegistryObject<Block> JUNGLE_BACKPACK_SHELF = register("jungle_backpack_shelf", () -> new ShelfBlock(Block.Properties.copy(Blocks.JUNGLE_PLANKS)));
    public static final RegistryObject<Block> DARK_OAK_BACKPACK_SHELF = register("dark_oak_backpack_shelf", () -> new ShelfBlock(Block.Properties.copy(Blocks.DARK_OAK_PLANKS)));
    public static final RegistryObject<Block> ACACIA_BACKPACK_SHELF = register("acacia_backpack_shelf", () -> new ShelfBlock(Block.Properties.copy(Blocks.ACACIA_PLANKS)));
    public static final RegistryObject<Block> CRIMSON_BACKPACK_SHELF = register("crimson_backpack_shelf", () -> new ShelfBlock(Block.Properties.copy(Blocks.CRIMSON_PLANKS)));
    public static final RegistryObject<Block> WARPED_BACKPACK_SHELF = register("warped_backpack_shelf", () -> new ShelfBlock(Block.Properties.copy(Blocks.WARPED_PLANKS)));

    private static <T extends Block> RegistryObject<T> register(String id, Supplier<T> block)
    {
        return register(id, block, b -> new BlockItem(b, new Item.Properties().tab(Backpacked.TAB)));
    }

    private static <T extends Block> RegistryObject<T> register(String id, Supplier<T> block, @Nullable Function<T, BlockItem> supplier)
    {
        RegistryObject<T> registryObject = ModBlocks.REGISTER.register(id, block);
        if(supplier != null)
        {
            ModItems.REGISTER.register(id, () -> supplier.apply(registryObject.get()));
        }
        return registryObject;
    }
}
