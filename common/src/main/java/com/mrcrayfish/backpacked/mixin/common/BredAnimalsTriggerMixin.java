package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.event.BackpackedEvents;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Simple injection into the bred trigger to post our own custom event
 * <p>
 * See {@link BackpackedEvents#BRED_ANIMAL} for relevant event
 * <p>
 * Author: MrCrayfish
 */
@Mixin(BredAnimalsTrigger.class)
public class BredAnimalsTriggerMixin
{
    @Inject(method = "trigger", at = @At(value = "HEAD"))
    public void backpacked$OnPlayerBreedTurtle(ServerPlayer player, Animal animal, Animal partner, AgeableMob ageableEntity, CallbackInfo ci)
    {
        BackpackedEvents.BRED_ANIMAL.post().handle(animal, partner, player);
    }
}
