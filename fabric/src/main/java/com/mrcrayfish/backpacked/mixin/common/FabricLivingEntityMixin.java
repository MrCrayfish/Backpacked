package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.entity.LootCapture;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(LivingEntity.class)
public class FabricLivingEntityMixin
{
    @Inject(method = "dropAllDeathLoot", at = @At(value = "HEAD"))
    private void backpacked$OnDropLootPre(DamageSource damageSource, CallbackInfo ci)
    {
        if(damageSource.getEntity() instanceof ServerPlayer player)
        {
            ((LootCapture) this).backpacked$StartCapturingDrop(player);
        }
    }

    @Inject(method = "dropAllDeathLoot", at = @At(value = "TAIL"))
    private void backpacked$OnDropLootPost(DamageSource damageSource, CallbackInfo ci)
    {
        ((LootCapture) this).backpacked$EndCapturingDrop();
    }

    @Inject(method = "die", at = @At(value = "TAIL"))
    private void backpacked$OnDie(DamageSource source, CallbackInfo ci)
    {
        // Back up just in-case other mods somehow cancel dropAllDeathLoot
        ((LootCapture) this).backpacked$EndCapturingDrop();
    }
}
