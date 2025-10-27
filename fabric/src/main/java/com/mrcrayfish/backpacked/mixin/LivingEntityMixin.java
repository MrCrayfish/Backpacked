package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.entity.LootCapture;
import net.minecraft.server.level.ServerLevel;
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
public class LivingEntityMixin
{
    @Inject(method = "dropAllDeathLoot", at = @At(value = "HEAD"))
    private void backpacked$OnDropLootPre(ServerLevel level, DamageSource source, CallbackInfo ci)
    {
        if(source.getEntity() instanceof ServerPlayer player)
        {
            ((LootCapture) this).backpacked$StartCapturingDrop(player);
        }
    }

    @Inject(method = "dropAllDeathLoot", at = @At(value = "TAIL"))
    private void backpacked$OnDropLootPost(ServerLevel level, DamageSource source, CallbackInfo ci)
    {
        ((LootCapture) this).backpacked$EndCapturingDrop();
    }

    @Inject(method = "die", at = @At(value = "TAIL"))
    private void backpacked$OnDropLootPost(DamageSource source, CallbackInfo ci)
    {
        // Back up just in-case other mods somehow cancel dropAllDeathLoot
        ((LootCapture) this).backpacked$EndCapturingDrop();
    }
}
