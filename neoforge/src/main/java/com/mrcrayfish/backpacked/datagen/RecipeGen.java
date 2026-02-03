package com.mrcrayfish.backpacked.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
public class RecipeGen extends RecipeProvider
{
    public RecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider)
    {
        super(output, provider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output)
    {
        CommonRecipeGen.generate(output, RecipeProvider::has, RecipeProvider::has, Tags.Items.INGOTS_IRON, Tags.Items.INGOTS_COPPER, Tags.Items.STRINGS, Tags.Items.RODS_WOODEN, Tags.Items.LEATHERS);
    }
}
