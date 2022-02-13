package com.mrcrayfish.backpacked.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.core.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class LootTableGen extends LootTableProvider
{
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> tables = ImmutableList.of(Pair.of(BlockProvider::new, LootContextParamSets.BLOCK));

    public LootTableGen(DataGenerator generator)
    {
        super(generator);
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext context) {}

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables()
    {
        return this.tables;
    }

    private static class BlockProvider extends BlockLoot
    {
        @Override
        protected void addTables()
        {
            this.dropSelf(ModBlocks.OAK_BACKPACK_SHELF.get());
            this.dropSelf(ModBlocks.SPRUCE_BACKPACK_SHELF.get());
            this.dropSelf(ModBlocks.BIRCH_BACKPACK_SHELF.get());
            this.dropSelf(ModBlocks.JUNGLE_BACKPACK_SHELF.get());
            this.dropSelf(ModBlocks.DARK_OAK_BACKPACK_SHELF.get());
            this.dropSelf(ModBlocks.ACACIA_BACKPACK_SHELF.get());
            this.dropSelf(ModBlocks.CRIMSON_BACKPACK_SHELF.get());
            this.dropSelf(ModBlocks.WARPED_BACKPACK_SHELF.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            return ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block.getRegistryName() != null && Reference.MOD_ID.equals(block.getRegistryName().getNamespace())).collect(Collectors.toSet());
        }
    }
}
