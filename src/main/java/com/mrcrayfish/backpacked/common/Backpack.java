package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.data.UnlockTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
        return UnlockTracker.get(player).map(impl -> impl.getUnlockedBackpacks().contains(this.id)).orElse(false);
    }

    @OnlyIn(Dist.CLIENT)
    public void clientTick(PlayerEntity player, Vector3d pos) {}

    @OnlyIn(Dist.CLIENT)
    public abstract BackpackModel getModel();
}
