package com.mrcrayfish.backpacked.datagen;

import com.mrcrayfish.backpacked.core.ModBlocks;
import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class CommonRecipeGen
{
    public static void generate(RecipeOutput output, Function<ItemLike, Criterion<?>> hasItem, Function<TagKey<Item>, Criterion<?>> hasTag, TagKey<Item> ironIngotTag, TagKey<Item> copperIngotTag, TagKey<Item> stringTag, TagKey<Item> stickTag, TagKey<Item> leatherTag)
    {
        backpack(output, hasTag, ironIngotTag, stringTag, leatherTag);
        backpackShelf(output, hasItem, hasTag, Items.OAK_SLAB, ModBlocks.OAK_BACKPACK_SHELF.get(), stickTag);
        backpackShelf(output, hasItem, hasTag, Items.SPRUCE_SLAB, ModBlocks.SPRUCE_BACKPACK_SHELF.get(), stickTag);
        backpackShelf(output, hasItem, hasTag, Items.BIRCH_SLAB, ModBlocks.BIRCH_BACKPACK_SHELF.get(), stickTag);
        backpackShelf(output, hasItem, hasTag, Items.JUNGLE_SLAB, ModBlocks.JUNGLE_BACKPACK_SHELF.get(), stickTag);
        backpackShelf(output, hasItem, hasTag, Items.DARK_OAK_SLAB, ModBlocks.DARK_OAK_BACKPACK_SHELF.get(), stickTag);
        backpackShelf(output, hasItem, hasTag, Items.ACACIA_SLAB, ModBlocks.ACACIA_BACKPACK_SHELF.get(), stickTag);
        backpackShelf(output, hasItem, hasTag, Items.CRIMSON_SLAB, ModBlocks.CRIMSON_BACKPACK_SHELF.get(), stickTag);
        backpackShelf(output, hasItem, hasTag, Items.WARPED_SLAB, ModBlocks.WARPED_BACKPACK_SHELF.get(), stickTag);
        backpackShelf(output, hasItem, hasTag, Items.CHERRY_SLAB, ModBlocks.CHERRY_BACKPACK_SHELF.get(), stickTag);
        backpackDock(output, hasItem, hasTag, copperIngotTag);
    }

    private static void backpack(RecipeOutput output, Function<TagKey<Item>, Criterion<?>> hasTag, TagKey<Item> ironIngotTag, TagKey<Item> stringTag, TagKey<Item> leatherTag)
    {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BACKPACK.get())
                .pattern("LLL")
                .pattern("SIS")
                .pattern("LLL")
                .define('L', leatherTag)
                .define('S', ironIngotTag)
                .define('I', stringTag)
                .unlockedBy("has_leather", hasTag.apply(leatherTag))
                .unlockedBy("has_string", hasTag.apply(stringTag))
                .unlockedBy("has_iron_ingot", hasTag.apply(ironIngotTag))
                .save(output);
    }

    private static void backpackShelf(RecipeOutput output, Function<ItemLike, Criterion<?>> hasItem, Function<TagKey<Item>, Criterion<?>> hasTag, ItemLike slab, ItemLike craftedItem, TagKey<Item> stickTag)
    {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, craftedItem, 4)
                .pattern("HHH")
                .pattern("S S")
                .define('H', slab)
                .define('S', stickTag)
                .unlockedBy("has_slab", hasItem.apply(slab))
                .unlockedBy("has_stick", hasTag.apply(stickTag))
                .save(output);
    }

    private static void backpackDock(RecipeOutput output, Function<ItemLike, Criterion<?>> hasItem, Function<TagKey<Item>, Criterion<?>> hasTag, TagKey<Item> copperIngotTag)
    {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.BACKPACK_DOCK.get())
                .pattern("PCP")
                .pattern("CHC")
                .pattern("PCP")
                .define('P', ItemTags.PLANKS)
                .define('C', copperIngotTag)
                .define('H', Items.HOPPER)
                .unlockedBy("has_planks", hasTag.apply(ItemTags.PLANKS))
                .unlockedBy("has_copper_ingot", hasTag.apply(copperIngotTag))
                .unlockedBy("has_hopper", hasItem.apply(Items.HOPPER))
                .save(output);
    }
}
