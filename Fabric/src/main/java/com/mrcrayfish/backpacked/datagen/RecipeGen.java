package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.core.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
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
        // Per platform recipe for the backpack since the Forge version uses custom Forge tags
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BACKPACK.get())
                .pattern("HHH")
                .pattern("SIS")
                .pattern("HHH")
                .define('H', Items.RABBIT_HIDE)
                .define('S', Items.STRING)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_hide", has(Items.RABBIT_HIDE))
                .save(consumer);

        // Apply common recipes
        CommonRecipeGen.generate(consumer, RecipeProvider::has);
    }
}
