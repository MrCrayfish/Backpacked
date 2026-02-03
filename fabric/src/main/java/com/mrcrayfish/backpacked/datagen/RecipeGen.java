package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.core.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class RecipeGen extends FabricRecipeProvider
{
    public RecipeGen(FabricDataOutput output)
    {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> consumer)
    {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BACKPACK.get())
            .pattern("LLL")
            .pattern("SIS")
            .pattern("LLL")
            .define('L', Items.LEATHER)
            .define('S', ConventionalItemTags.IRON_INGOTS)
            .define('I', Items.STRING)
            .unlockedBy("has_leather", has(Items.LEATHER))
            .unlockedBy("has_string", has(Items.STRING))
            .unlockedBy("has_iron_ingot", has(ConventionalItemTags.IRON_INGOTS))
            .save(consumer);

        // Apply common recipes
        CommonRecipeGen.generate(consumer, RecipeProvider::has, RecipeProvider::has, ConventionalItemTags.COPPER_INGOTS);
    }
}
