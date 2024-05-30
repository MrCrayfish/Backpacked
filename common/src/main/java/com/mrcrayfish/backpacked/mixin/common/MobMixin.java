package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.event.BackpackedEntityEvents;
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

/**
 * Author: MrCrayfish
 */
@Mixin(Mob.class)
public class MobMixin
{
    @Unique
    private ItemStack backpacked$capturedFood;

    @Inject(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private void backpackedOnMobInteractPre(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir)
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

    @Inject(method = "interact", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/Mob;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
    private void backpackedOnMobInteractPost(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir)
    {
        if(this.backpacked$capturedFood != null)
        {
            ItemStack heldItem = player.getItemInHand(hand);
            if(!ItemStack.matches(this.backpacked$capturedFood, heldItem))
            {
                Mob mob = (Mob) (Object) this;
                if(mob instanceof Animal animal)
                {
                    BackpackedEntityEvents.FEED_ANIMAL.post().handle(animal, player);
                }
            }
            this.backpacked$capturedFood = null;
        }
    }
}
