package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.UnlockTracker;
import com.mrcrayfish.backpacked.common.backpack.TurtleShellBackpack;
import com.mrcrayfish.backpacked.common.tracker.CountProgressTracker;
import net.minecraft.advancements.criterion.BredAnimalsTrigger;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(BredAnimalsTrigger.class)
public class BredAnimalsTriggerMixin
{
    @Inject(method = "trigger", at = @At(value = "HEAD"))
    public void onPlayerBreedTurtle(ServerPlayerEntity player, AnimalEntity animal, AnimalEntity partner, AgeableEntity ageableEntity, CallbackInfo ci)
    {
        if((!(animal instanceof TurtleEntity)))
            return;

        UnlockTracker.get(player).ifPresent(unlockTracker ->
        {
            unlockTracker.getProgressTracker(TurtleShellBackpack.ID).ifPresent(progressTracker ->
            {
                CountProgressTracker tracker = (CountProgressTracker) progressTracker;
                tracker.increment(player);
            });
        });
    }
}
