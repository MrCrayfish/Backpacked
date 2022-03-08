package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.ModelSupplier;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.ProgressFormatters;
import com.mrcrayfish.backpacked.common.UnlockTracker;
import com.mrcrayfish.backpacked.common.tracker.CountProgressTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class EndCrystalBackpack extends Backpack
{
    public static final ResourceLocation ID = new ResourceLocation(Reference.MOD_ID, "end_crystal");

    public EndCrystalBackpack()
    {
        super(ID);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ModelSupplier getModelSupplier()
    {
        return () -> ModelInstances.END_CRYSTAL;
    }

    @Nullable
    @Override
    protected IProgressTracker createProgressTracker()
    {
        return new CountProgressTracker(1, ProgressFormatters.INCOMPLETE_COMPLETE);
    }

    @SubscribeEvent
    public void onKillLivingEntity(LivingDeathEvent event)
    {
        Entity sourceEntity = event.getSource().getEntity();
        if(event.getEntityLiving() instanceof EnderDragonEntity && sourceEntity instanceof ServerPlayerEntity)
        {
            UnlockTracker.get((PlayerEntity) sourceEntity).ifPresent(unlockTracker ->
            {
                unlockTracker.getProgressTracker(ID).ifPresent(progressTracker ->
                {
                    ((CountProgressTracker) progressTracker).increment((ServerPlayerEntity) sourceEntity);
                });
            });
        }
    }
}
