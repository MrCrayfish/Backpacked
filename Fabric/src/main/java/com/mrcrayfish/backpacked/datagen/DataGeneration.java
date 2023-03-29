package com.mrcrayfish.backpacked.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * Author: MrCrayfish
 */
public class DataGeneration implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator)
    {
        generator.addProvider(BlockTagGen::new);
        generator.addProvider(LootTableGen::new);
        generator.addProvider(RecipeGen::new);
    }
}
