package com.mrcrayfish.backpacked.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

/**
 * Author: MrCrayfish
 */
public class LootTableGen extends FabricBlockLootSubProvider
{
    protected LootTableGen(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture)
    {
        super(dataOutput, registriesFuture);
    }

    @Override
    public void generate()
    {
        CommonLootTableGen.generate(this::dropSelf);
    }
}
