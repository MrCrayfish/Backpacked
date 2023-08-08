package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.core.ModBlocks;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class CommonRecipeGen
{
    public static void generate(Consumer<FinishedRecipe> consumer, Function<ItemLike, CriterionTriggerInstance> has)
    {
        backpackShelf(consumer, has, Items.OAK_LOG, Items.OAK_SLAB, ModBlocks.OAK_BACKPACK_SHELF.get());
        backpackShelf(consumer, has, Items.SPRUCE_LOG, Items.SPRUCE_SLAB, ModBlocks.SPRUCE_BACKPACK_SHELF.get());
        backpackShelf(consumer, has, Items.BIRCH_LOG, Items.BIRCH_SLAB, ModBlocks.BIRCH_BACKPACK_SHELF.get());
        backpackShelf(consumer, has, Items.JUNGLE_LOG, Items.JUNGLE_SLAB, ModBlocks.JUNGLE_BACKPACK_SHELF.get());
        backpackShelf(consumer, has, Items.DARK_OAK_LOG, Items.DARK_OAK_SLAB, ModBlocks.DARK_OAK_BACKPACK_SHELF.get());
        backpackShelf(consumer, has, Items.ACACIA_LOG, Items.ACACIA_SLAB, ModBlocks.ACACIA_BACKPACK_SHELF.get());
        backpackShelf(consumer, has, Items.CRIMSON_STEM, Items.CRIMSON_SLAB, ModBlocks.CRIMSON_BACKPACK_SHELF.get());
        backpackShelf(consumer, has, Items.WARPED_STEM, Items.WARPED_SLAB, ModBlocks.WARPED_BACKPACK_SHELF.get());
        backpackShelf(consumer, has, Items.CHERRY_LOG, Items.CHERRY_SLAB, ModBlocks.CHERRY_BACKPACK_SHELF.get());
    }

    private static void backpackShelf(Consumer<FinishedRecipe> consumer, Function<ItemLike, CriterionTriggerInstance> has, ItemLike log, ItemLike slab, ItemLike craftedItem)
    {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, craftedItem, 4)
                .pattern("LHL")
                .pattern("S S")
                .define('L', log)
                .define('H', slab)
                .define('S', Items.STICK)
                .unlockedBy("has_slab", has.apply(slab))
                .unlockedBy("has_stick", has.apply(Items.STICK))
                .save(consumer);
    }
}
