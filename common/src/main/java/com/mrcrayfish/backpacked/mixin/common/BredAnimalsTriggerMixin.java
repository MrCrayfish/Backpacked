package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.backpack.impl.TurtleShellBackpack;
import com.mrcrayfish.backpacked.data.tracker.UnlockManager;
import com.mrcrayfish.backpacked.data.tracker.impl.CountProgressTracker;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Turtle;
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
    public void backpackedOnPlayerBreedTurtle(ServerPlayer player, Animal animal, Animal partner, AgeableMob ageableEntity, CallbackInfo ci)
    {
        if((!(animal instanceof Turtle)))
            return;

        UnlockManager.getTracker(player).flatMap(tracker -> tracker.getProgressTracker(TurtleShellBackpack.ID)).ifPresent(tracker -> {
            CountProgressTracker countTracker = (CountProgressTracker) tracker;
            countTracker.increment(player);
        });
    }
}
