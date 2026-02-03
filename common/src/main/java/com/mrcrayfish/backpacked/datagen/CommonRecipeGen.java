package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.core.ModBlocks;
import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class CommonRecipeGen
{
    public static void generate(Consumer<FinishedRecipe> consumer, Function<ItemLike, CriterionTriggerInstance> hasItem, Function<TagKey<Item>, CriterionTriggerInstance> hasTag)
    {
        backpack(consumer, hasItem);
        backpackShelf(consumer, hasItem, Items.OAK_LOG, Items.OAK_SLAB, ModBlocks.OAK_BACKPACK_SHELF.get());
        backpackShelf(consumer, hasItem, Items.SPRUCE_LOG, Items.SPRUCE_SLAB, ModBlocks.SPRUCE_BACKPACK_SHELF.get());
        backpackShelf(consumer, hasItem, Items.BIRCH_LOG, Items.BIRCH_SLAB, ModBlocks.BIRCH_BACKPACK_SHELF.get());
        backpackShelf(consumer, hasItem, Items.JUNGLE_LOG, Items.JUNGLE_SLAB, ModBlocks.JUNGLE_BACKPACK_SHELF.get());
        backpackShelf(consumer, hasItem, Items.DARK_OAK_LOG, Items.DARK_OAK_SLAB, ModBlocks.DARK_OAK_BACKPACK_SHELF.get());
        backpackShelf(consumer, hasItem, Items.ACACIA_LOG, Items.ACACIA_SLAB, ModBlocks.ACACIA_BACKPACK_SHELF.get());
        backpackShelf(consumer, hasItem, Items.CRIMSON_STEM, Items.CRIMSON_SLAB, ModBlocks.CRIMSON_BACKPACK_SHELF.get());
        backpackShelf(consumer, hasItem, Items.WARPED_STEM, Items.WARPED_SLAB, ModBlocks.WARPED_BACKPACK_SHELF.get());
        backpackShelf(consumer, hasItem, Items.CHERRY_LOG, Items.CHERRY_SLAB, ModBlocks.CHERRY_BACKPACK_SHELF.get());
        backpackDock(consumer, hasItem, hasTag);
    }

    private static void backpack(Consumer<FinishedRecipe> consumer, Function<ItemLike, CriterionTriggerInstance> has)
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
            .save(consumer);
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

    private static void backpackDock(Consumer<FinishedRecipe> consumer, Function<ItemLike, CriterionTriggerInstance> hasItem, Function<TagKey<Item>, CriterionTriggerInstance> hasTag)
    {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BACKPACK_DOCK.get())
            .pattern("PCP")
            .pattern("CHC")
            .pattern("PCP")
            .define('P', ItemTags.PLANKS)
            .define('C', Items.COPPER_INGOT)
            .define('H', Items.HOPPER)
            .unlockedBy("has_planks", hasTag.apply(ItemTags.PLANKS))
            .unlockedBy("has_copper_ingot", hasItem.apply(Items.COPPER_INGOT))
            .unlockedBy("has_hopper", hasItem.apply(Items.HOPPER))
            .save(consumer);
    }
}
