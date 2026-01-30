package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
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
        // Apply common recipes
        CommonRecipeGen.generate(consumer, RecipeProvider::has, RecipeProvider::has);
    }
}
