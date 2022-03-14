package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.common.Backpack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ClassicBackpack extends Backpack
{
    public ClassicBackpack()
    {
        super(new ResourceLocation(Reference.MOD_ID, "classic"));
    }

    @Override
    public boolean isUnlocked(Player player)
    {
        return true;
    }

    @Override
    public Supplier<Object> getModelSupplier()
    {
        return ClientHandler.getModelInstances()::getClassic;
    }
}
