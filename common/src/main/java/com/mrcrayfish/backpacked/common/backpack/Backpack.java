package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.data.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.data.tracker.UnlockManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

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

    public boolean isUnlocked(Player player)
    {
        return UnlockManager.get(player).map(impl -> impl.getUnlockedBackpacks().contains(this.id)).orElse(false) || Config.SERVER.common.unlockAllBackpacks.get();
    }

    public void clientTick(Player player, Vec3 pos) {}

    public abstract Supplier<Object> getModelSupplier();

    @Nullable
    public IProgressTracker createProgressTracker()
    {
        return null;
    }
}
