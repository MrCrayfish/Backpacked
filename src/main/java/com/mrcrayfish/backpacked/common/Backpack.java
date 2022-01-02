package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    @OnlyIn(Dist.CLIENT)
    public void clientTick(PlayerEntity player, Vector3d pos) {}

    @OnlyIn(Dist.CLIENT)
    public abstract BackpackModel getModel();

    @Nullable
    protected IProgressTracker createProgressTracker()
    {
        return null;
    }
}
