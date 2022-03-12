package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Config;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.function.Supplier;

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

    /**
     * This should return an instance of BackpackModel. The method supplies as an object to prevent
     * dedicated servers from loading client only classes, which would result in a crash. This method
     * is intended for client only.
     *
     * @return an instance of {@link com.mrcrayfish.backpacked.client.model.BackpackModel}
     */
    public abstract Supplier<Object> getModelSupplier();

    @Nullable
    protected IProgressTracker createProgressTracker()
    {
        return null;
    }
}
