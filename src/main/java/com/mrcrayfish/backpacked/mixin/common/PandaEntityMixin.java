package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.UnlockTracker;
import com.mrcrayfish.backpacked.common.backpack.BambooBasketBackpack;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Author: MrCrayfish
 */
@Mixin(PandaEntity.class)
public class PandaEntityMixin
{
    @Inject(method = "mobInteract", at = @At(value = "RETURN", ordinal = 3))
    public void onFeedPanda(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResultType> cir)
    {
        if(!(player instanceof ServerPlayerEntity))
            return;

        UnlockTracker.get(player).ifPresent(unlockTracker ->
        {
            unlockTracker.getProgressTracker(BambooBasketBackpack.ID).ifPresent(tracker ->
            {
                BambooBasketBackpack.ProgressTracker progressTracker = (BambooBasketBackpack.ProgressTracker) tracker;
                progressTracker.addPanda((PandaEntity) (Object) this, (ServerPlayerEntity) player);
            });
        });
    }
}
