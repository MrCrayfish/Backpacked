package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.backpack.impl.BambooBasketBackpack;
import com.mrcrayfish.backpacked.data.tracker.UnlockManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Author: MrCrayfish
 */
@Mixin(Panda.class)
public class PandaEntityMixin
{
    @Inject(method = "mobInteract", at = @At(value = "RETURN", ordinal = 3))
    public void backpackedOnFeedPanda(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir)
    {
        if(!(player instanceof ServerPlayer))
            return;

        UnlockManager.getTracker(player).flatMap(tracker -> tracker.getProgressTracker(BambooBasketBackpack.ID)).ifPresent(tracker -> {
            BambooBasketBackpack.ProgressTracker progressTracker = (BambooBasketBackpack.ProgressTracker) tracker;
            progressTracker.addPanda((Panda) (Object) this, (ServerPlayer) player);
        });
    }
}
