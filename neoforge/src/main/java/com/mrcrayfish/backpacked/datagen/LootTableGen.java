package com.mrcrayfish.backpacked.datagen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.framework.Registration;
import com.mrcrayfish.framework.api.registry.BlockRegistryEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class LootTableGen extends LootTableProvider
{
    public LootTableGen(PackOutput output, CompletableFuture<HolderLookup.Provider> provider)
    {
        super(output, Collections.emptySet(), ImmutableList.of(new SubProviderEntry(BlockProvider::new, LootContextParamSets.BLOCK)), provider);
    }

    @Override
    protected void validate(WritableRegistry<LootTable> registry, ValidationContext context, ProblemReporter.Collector collector) {}

    private static class BlockProvider extends BlockLootSubProvider
    {
        protected BlockProvider(HolderLookup.Provider provider)
        {
            super(ImmutableSet.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        protected void generate()
        {
            CommonLootTableGen.generate(this::dropSelf);
        }

        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            return Registration.get(Registries.BLOCK).stream().filter(entry -> entry.getId().getNamespace().equals(Constants.MOD_ID)).map(entry -> ((BlockRegistryEntry<?, ?>) entry).get()).collect(Collectors.toSet());
        }
    }
}
