package com.mrcrayfish.backpacked.common.backpack.impl;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatters;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import com.mrcrayfish.framework.api.event.EntityEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class EndCrystalBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "end_crystal");

    public EndCrystalBackpack()
    {
        super(null, null);
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
            UnlockManager.getTracker(player).flatMap(tracker -> tracker.getProgressTracker(ID)).ifPresent(tracker -> {
                ((CountProgressTracker) tracker).increment(player);
            });
        }
        return false;
    }
}
