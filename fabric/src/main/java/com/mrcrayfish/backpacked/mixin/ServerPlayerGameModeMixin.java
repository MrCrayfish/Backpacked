package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.common.backpack.impl.CardboardBoxBackpack;
import com.mrcrayfish.backpacked.data.tracker.UnlockManager;
import com.mrcrayfish.backpacked.data.tracker.impl.CountProgressTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
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
public class ServerPlayerGameModeMixin
{
    @Shadow
    @Final
    protected ServerPlayer player;

    @Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void backpackedAfterBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockEntity blockEntity, Block block, BlockState state, BlockState removed)
    {
        if(state.is(BlockTags.LOGS))
        {
            UnlockManager.getTracker(this.player).flatMap(tracker -> tracker.getProgressTracker(CardboardBoxBackpack.ID)).ifPresent(tracker -> {
                CountProgressTracker countTracker = (CountProgressTracker) tracker;
                countTracker.increment(this.player);
            });
        }
    }
}
