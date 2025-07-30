package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.core.ModBlocks;
import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class CommonRecipeGen
{
    public static void generate(RecipeOutput output, Function<ItemLike, Criterion<?>> has)
    {
        backpack(output, has);
        backpackShelf(output, has, Items.OAK_LOG, Items.OAK_SLAB, ModBlocks.OAK_BACKPACK_SHELF.get());
        backpackShelf(output, has, Items.SPRUCE_LOG, Items.SPRUCE_SLAB, ModBlocks.SPRUCE_BACKPACK_SHELF.get());
        backpackShelf(output, has, Items.BIRCH_LOG, Items.BIRCH_SLAB, ModBlocks.BIRCH_BACKPACK_SHELF.get());
        backpackShelf(output, has, Items.JUNGLE_LOG, Items.JUNGLE_SLAB, ModBlocks.JUNGLE_BACKPACK_SHELF.get());
        backpackShelf(output, has, Items.DARK_OAK_LOG, Items.DARK_OAK_SLAB, ModBlocks.DARK_OAK_BACKPACK_SHELF.get());
        backpackShelf(output, has, Items.ACACIA_LOG, Items.ACACIA_SLAB, ModBlocks.ACACIA_BACKPACK_SHELF.get());
        backpackShelf(output, has, Items.CRIMSON_STEM, Items.CRIMSON_SLAB, ModBlocks.CRIMSON_BACKPACK_SHELF.get());
        backpackShelf(output, has, Items.WARPED_STEM, Items.WARPED_SLAB, ModBlocks.WARPED_BACKPACK_SHELF.get());
        backpackShelf(output, has, Items.CHERRY_LOG, Items.CHERRY_SLAB, ModBlocks.CHERRY_BACKPACK_SHELF.get());
    }

    private static void backpack(RecipeOutput output, Function<ItemLike, Criterion<?>> has)
    {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BACKPACK.get())
                .pattern("LLL")
                .pattern("SIS")
                .pattern("LLL")
                .define('L', Items.LEATHER)
                .define('S', Items.STRING)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_leather", has.apply(Items.LEATHER))
                .unlockedBy("has_string", has.apply(Items.STRING))
                .unlockedBy("has_iron_ingot", has.apply(Items.IRON_INGOT))
                .save(output);
    }

    private static void backpackShelf(RecipeOutput output, Function<ItemLike, Criterion<?>> has, ItemLike log, ItemLike slab, ItemLike craftedItem)
    {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, craftedItem, 4)
                .pattern("LHL")
                .pattern("S S")
                .define('L', log)
                .define('H', slab)
                .define('S', Items.STICK)
                .unlockedBy("has_slab", has.apply(slab))
                .unlockedBy("has_stick", has.apply(Items.STICK))
                .save(output);
    }
}
