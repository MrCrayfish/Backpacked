package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.event.BackpackedEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * This mixin injects a detection method to determine if the player fed an animal. This basically
 * just checks if the item after the interaction is different. If a change is detected, an event is
 * posted.
 * <p>
 * See {@link BackpackedEvents#FEED_ANIMAL} for relevant event
 * <p>
 * Author: MrCrayfish
 */
@Mixin(Mob.class)
public class MobMixin
{
    @Unique
    private ItemStack backpacked$capturedFood;

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private void backpacked$OnMobInteractPre(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir)
    {
        Mob mob = (Mob) (Object) this;
        if(mob instanceof Animal animal)
        {
            ItemStack heldItem = player.getItemInHand(hand);
            if(animal.isFood(heldItem))
            {
                this.backpacked$capturedFood = player.getItemInHand(hand).copy();
            }
        }
    }

    @Inject(method = "interact", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/Mob;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void backpacked$OnMobInteractPost(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, InteractionResult result)
    {
        if(result.consumesAction() && this.backpacked$capturedFood != null)
        {
            Mob mob = (Mob) (Object) this;
            if(mob instanceof Animal animal)
            {
                BackpackedEvents.FEED_ANIMAL.post().handle(animal, player);
            }
        }
        this.backpacked$capturedFood = null;
    }
}
