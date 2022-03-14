package com.mrcrayfish.backpacked.common.backpack;

import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.common.Backpack;
import com.mrcrayfish.backpacked.common.IProgressTracker;
import com.mrcrayfish.backpacked.common.ProgressFormatters;
import com.mrcrayfish.backpacked.common.UnlockTracker;
import com.mrcrayfish.backpacked.common.tracker.CountProgressTracker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

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
    public Supplier<Object> getModelSupplier()
    {
        return ClientHandler.getModelInstances()::getEndCrystal;
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
        if(event.getEntityLiving() instanceof EnderDragon && sourceEntity instanceof ServerPlayer player)
        {
            UnlockTracker.get(player).ifPresent(unlockTracker ->
            {
                unlockTracker.getProgressTracker(ID).ifPresent(progressTracker ->
                {
                    ((CountProgressTracker) progressTracker).increment(player);
                });
            });
        }
    }
}
