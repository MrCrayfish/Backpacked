package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.ModelSupplier;
import com.mrcrayfish.backpacked.common.Backpack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
public class StandardBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "standard");

    public StandardBackpack()
    {
        super(ID);
    }

    @Override
    public boolean isUnlocked(PlayerEntity player)
    {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ModelSupplier getModelSupplier()
    {
        return () -> ModelInstances.STANDARD;
    }
}
