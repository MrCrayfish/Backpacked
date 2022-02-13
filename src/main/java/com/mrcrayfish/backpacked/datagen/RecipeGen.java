package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.core.ModBlocks;
import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class RecipeGen extends RecipeProvider
{
    public RecipeGen(DataGenerator generator)
    {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)
    {
        ShapedRecipeBuilder.shaped(ModItems.BACKPACK.get())
                .pattern("HHH")
                .pattern("SIS")
                .pattern("HHH")
                .define('H', Items.RABBIT_HIDE)
                .define('S', Tags.Items.STRING)
                .define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_hide", has(Items.RABBIT_HIDE))
                .save(consumer);

        backpackShelf(consumer, Items.OAK_LOG, Items.OAK_SLAB, ModBlocks.OAK_BACKPACK_SHELF.get());
        backpackShelf(consumer, Items.SPRUCE_LOG, Items.SPRUCE_SLAB, ModBlocks.SPRUCE_BACKPACK_SHELF.get());
        backpackShelf(consumer, Items.BIRCH_LOG, Items.BIRCH_SLAB, ModBlocks.BIRCH_BACKPACK_SHELF.get());
        backpackShelf(consumer, Items.JUNGLE_LOG, Items.JUNGLE_SLAB, ModBlocks.JUNGLE_BACKPACK_SHELF.get());
        backpackShelf(consumer, Items.DARK_OAK_LOG, Items.DARK_OAK_SLAB, ModBlocks.DARK_OAK_BACKPACK_SHELF.get());
        backpackShelf(consumer, Items.ACACIA_LOG, Items.ACACIA_SLAB, ModBlocks.ACACIA_BACKPACK_SHELF.get());
        backpackShelf(consumer, Items.CRIMSON_STEM, Items.CRIMSON_SLAB, ModBlocks.CRIMSON_BACKPACK_SHELF.get());
        backpackShelf(consumer, Items.WARPED_STEM, Items.WARPED_SLAB, ModBlocks.WARPED_BACKPACK_SHELF.get());
    }

    private static void backpackShelf(Consumer<FinishedRecipe> consumer, ItemLike log, ItemLike slab, ItemLike craftedItem)
    {
        ShapedRecipeBuilder.shaped(craftedItem, 4)
                .pattern("LHL")
                .pattern("S S")
                .define('L', log)
                .define('H', slab)
                .define('S', Items.STICK)
                .unlockedBy("has_slab", has(slab))
                .unlockedBy("has_stick", has(Items.STICK))
                .save(consumer);
    }
}
