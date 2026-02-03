package com.mrcrayfish.backpacked.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

/**
 * Author: MrCrayfish
 */
public class LootTableGen extends FabricBlockLootTableProvider
{
    protected LootTableGen(FabricDataOutput dataOutput)
    {
        super(dataOutput);
    }

    @Override
    public void generate()
    {
        CommonLootTableGen.generate(this::dropSelf);
    }
}
