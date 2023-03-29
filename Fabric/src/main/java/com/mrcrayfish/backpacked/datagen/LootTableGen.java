package com.mrcrayfish.backpacked.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

/**
 * Author: MrCrayfish
 */
public class LootTableGen extends FabricBlockLootTableProvider
{
    protected LootTableGen(FabricDataGenerator generator)
    {
        super(generator);
    }

    @Override
    protected void generateBlockLootTables()
    {
        CommonLootTableGen.generate(this::dropSelf);
    }
}
