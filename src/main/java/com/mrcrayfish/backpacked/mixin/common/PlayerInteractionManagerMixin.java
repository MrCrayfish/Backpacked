package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.UnlockTracker;
import com.mrcrayfish.backpacked.common.backpack.CardboardBoxBackpack;
import com.mrcrayfish.backpacked.common.tracker.CountProgressTracker;
import net.minecraft.block.BlockState;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Author: MrCrayfish
 */
@Mixin(PlayerInteractionManager.class)
public class PlayerInteractionManagerMixin
{
    @Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/server/ServerWorld;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/tileentity/TileEntity;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void afterBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState state, int exp)
    {
        if(state.getBlock().is(BlockTags.LOGS))
        {
            PlayerInteractionManager manager = (PlayerInteractionManager) (Object) this;
            UnlockTracker.get(manager.player).ifPresent(unlockTracker ->
            {
                unlockTracker.getProgressTracker(CardboardBoxBackpack.ID).ifPresent(progressTracker ->
                {
                    CountProgressTracker tracker = (CountProgressTracker) progressTracker;
                    tracker.increment(manager.player);
                });
            });
        }
    }
}
