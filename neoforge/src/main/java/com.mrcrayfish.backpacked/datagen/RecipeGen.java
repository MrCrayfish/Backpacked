package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

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
    protected void buildRecipes(RecipeOutput output)
    {
        // Since we use Forge tags, we have to do it here
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BACKPACK.get())
                .pattern("HHH")
                .pattern("SIS")
                .pattern("HHH")
                .define('H', Items.RABBIT_HIDE)
                .define('S', Tags.Items.STRING)
                .define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_hide", has(Items.RABBIT_HIDE))
                .save(output);

        // Apply common recipes
        CommonRecipeGen.generate(output, RecipeProvider::has);
    }
}
