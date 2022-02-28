package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.client.ModelSupplier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class Backpack
{
    private final ResourceLocation id;

    public Backpack(ResourceLocation id)
    {
        this.id = id;
    }

    public ResourceLocation getId()
    {
        return this.id;
    }

    public boolean isUnlocked(PlayerEntity player)
    {
        return UnlockTracker.get(player).map(impl -> impl.getUnlockedBackpacks().contains(this.id)).orElse(false) || Config.SERVER.unlockAllBackpacks.get();
    }

    public void clientTick(PlayerEntity player, Vector3d pos) {}

    public abstract ModelSupplier getModelSupplier();

    @Nullable
    protected IProgressTracker createProgressTracker()
    {
        return null;
    }
}
