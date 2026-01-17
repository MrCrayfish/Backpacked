package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.core.ModBlocks;
import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
public class RecipeGen extends RecipeProvider
{
    public RecipeGen(HolderLookup.Provider provider, RecipeOutput output)
    {
        super(provider, output);
    }

    @Override
    public void buildRecipes()
    {
        this.backpack();
        this.backpackShelf(Items.OAK_LOG, Items.OAK_SLAB, ModBlocks.OAK_BACKPACK_SHELF.get());
        this.backpackShelf(Items.SPRUCE_LOG, Items.SPRUCE_SLAB, ModBlocks.SPRUCE_BACKPACK_SHELF.get());
        this.backpackShelf(Items.BIRCH_LOG, Items.BIRCH_SLAB, ModBlocks.BIRCH_BACKPACK_SHELF.get());
        this.backpackShelf(Items.JUNGLE_LOG, Items.JUNGLE_SLAB, ModBlocks.JUNGLE_BACKPACK_SHELF.get());
        this.backpackShelf(Items.DARK_OAK_LOG, Items.DARK_OAK_SLAB, ModBlocks.DARK_OAK_BACKPACK_SHELF.get());
        this.backpackShelf(Items.ACACIA_LOG, Items.ACACIA_SLAB, ModBlocks.ACACIA_BACKPACK_SHELF.get());
        this.backpackShelf(Items.CRIMSON_STEM, Items.CRIMSON_SLAB, ModBlocks.CRIMSON_BACKPACK_SHELF.get());
        this.backpackShelf(Items.WARPED_STEM, Items.WARPED_SLAB, ModBlocks.WARPED_BACKPACK_SHELF.get());
        this.backpackShelf(Items.CHERRY_LOG, Items.CHERRY_SLAB, ModBlocks.CHERRY_BACKPACK_SHELF.get());
        this.backpackDock();
    }

    private void backpack()
    {
        this.shaped(RecipeCategory.TOOLS, ModItems.BACKPACK.get())
            .pattern("LLL")
            .pattern("SIS")
            .pattern("LLL")
            .define('L', Items.LEATHER)
            .define('S', Items.STRING)
            .define('I', Items.IRON_INGOT)
            .unlockedBy("has_leather", this.has(Items.LEATHER))
            .unlockedBy("has_string", this.has(Items.STRING))
            .unlockedBy("has_iron_ingot", this.has(Items.IRON_INGOT))
            .save(this.output);
    }

    private void backpackShelf(ItemLike log, ItemLike slab, ItemLike craftedItem)
    {
        this.shaped(RecipeCategory.DECORATIONS, craftedItem, 4)
            .pattern("LHL")
            .pattern("S S")
            .define('L', log)
            .define('H', slab)
            .define('S', Items.STICK)
            .unlockedBy("has_slab", this.has(slab))
            .unlockedBy("has_stick", this.has(Items.STICK))
            .save(this.output);
    }

    private void backpackDock()
    {
        this.shaped(RecipeCategory.DECORATIONS, ModBlocks.BACKPACK_DOCK.get())
            .pattern("PCP")
            .pattern("CHC")
            .pattern("PCP")
            .define('P', ItemTags.PLANKS)
            .define('C', Items.COPPER_INGOT)
            .define('H', Items.HOPPER)
            .unlockedBy("has_planks", this.has(ItemTags.PLANKS))
            .unlockedBy("has_copper_ingot", this.has(Items.COPPER_INGOT))
            .unlockedBy("has_hopper", this.has(Items.HOPPER))
            .save(this.output);
    }

    public static class Runner extends RecipeProvider.Runner
    {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
        {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput output)
        {
            return new RecipeGen(provider, output);
        }

        @Override
        public String getName()
        {
            return "Backpacked Recipes";
        }
    }
}
