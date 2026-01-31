package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin
{
    @Shadow
    public AbstractArrow.Pickup pickup;

    @Inject(method = "tryPickup", at = @At(value = "HEAD"), cancellable = true)
    private void backpacked$AddArrowToBackpack(Player player, CallbackInfoReturnable<Boolean> cir)
    {
        // Respect the rules of the arrow and prevent picking up if not allowed
        if(this.pickup == AbstractArrow.Pickup.ALLOWED)
        {
            if(AugmentHandler.beforeArrowPickup(player, (AbstractArrow) (Object) this))
            {
                cir.setReturnValue(true);
            }
        }
    }
}
