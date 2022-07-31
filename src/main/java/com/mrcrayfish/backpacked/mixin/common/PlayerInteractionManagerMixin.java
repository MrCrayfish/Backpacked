package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.UnlockTracker;
import com.mrcrayfish.backpacked.common.backpack.CardboardBoxBackpack;
import com.mrcrayfish.backpacked.common.tracker.CountProgressTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Author: MrCrayfish
 */
@Mixin(ServerPlayerGameMode.class)
public class PlayerInteractionManagerMixin
{
    @Shadow
    @Final
    protected ServerPlayer player;

    @Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayerGameMode;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void afterBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState state, int exp)
    {
        if(state.is(BlockTags.LOGS))
        {
            UnlockTracker.get(this.player).ifPresent(unlockTracker ->
            {
                unlockTracker.getProgressTracker(CardboardBoxBackpack.ID).ifPresent(progressTracker ->
                {
                    CountProgressTracker tracker = (CountProgressTracker) progressTracker;
                    tracker.increment(this.player);
                });
            });
        }
    }
}
