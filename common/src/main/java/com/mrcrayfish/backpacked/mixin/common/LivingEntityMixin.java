package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.challenge.impl.KillMobChallenge;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @Shadow
    protected boolean dead;

    @Inject(method = "die", at = @At(value = "TAIL"))
    private void backpacked$OnDeath(DamageSource source, CallbackInfo ci)
    {
        LivingEntity entity = (LivingEntity) (Object) this;
        if(this.dead && !entity.isRemoved() && !entity.level().isClientSide())
        {
            KillMobChallenge.Tracker.onLivingEntityDeath(entity, source);
        }
    }
}
