package com.mrcrayfish.backpacked.common.backpack.impl;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.model.ModelInstances;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.data.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.data.tracker.ProgressFormatters;
import com.mrcrayfish.backpacked.data.tracker.UnlockManager;
import com.mrcrayfish.backpacked.data.tracker.impl.CountProgressTracker;
import com.mrcrayfish.framework.api.event.EntityEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class EndCrystalBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "end_crystal");

    public EndCrystalBackpack()
    {
        super(null);
        EntityEvents.LIVING_ENTITY_DEATH.register(this::onLivingEntityDeath);
    }

    @Nullable
    @Override
    public IProgressTracker createProgressTracker()
    {
        return new CountProgressTracker(1, ProgressFormatters.INCOMPLETE_COMPLETE);
    }

    private boolean onLivingEntityDeath(LivingEntity entity, DamageSource source)
    {
        Entity sourceEntity = source.getEntity();
        if(entity instanceof EnderDragon && sourceEntity instanceof ServerPlayer player)
        {
            UnlockManager.get(player).flatMap(tracker -> tracker.getProgressTracker(ID)).ifPresent(tracker -> {
                ((CountProgressTracker) tracker).increment(player);
            });
        }
        return false;
    }
}
