package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class RecipeGen extends RecipeProvider
{
    public RecipeGen(PackOutput output)
    {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer)
    {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BACKPACK.get())
            .pattern("LLL")
            .pattern("SIS")
            .pattern("LLL")
            .define('L', Tags.Items.LEATHER)
            .define('S', Tags.Items.INGOTS_IRON)
            .define('I', Tags.Items.STRING)
            .unlockedBy("has_leather", has(Tags.Items.LEATHER))
            .unlockedBy("has_string", has(Tags.Items.STRING))
            .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
            .save(consumer);

        // Apply common recipes
        CommonRecipeGen.generate(consumer, RecipeProvider::has, RecipeProvider::has, Tags.Items.INGOTS_COPPER);
    }
}
