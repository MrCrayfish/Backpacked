package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.UnlockTracker;
import com.mrcrayfish.backpacked.common.backpack.HoneyJarBackpack;
import com.mrcrayfish.backpacked.common.tracker.CountProgressTracker;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Author: MrCrayfish
 */
@Mixin(BeehiveBlock.class)
public class BeehiveBlockMixin
{
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;shrink(I)V"))
    public void onGatherHoneyBottle(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result, CallbackInfoReturnable<ActionResultType> cir)
    {
        if(player instanceof ServerPlayerEntity)
        {
            UnlockTracker.get(player).ifPresent(unlockTracker ->
            {
                unlockTracker.getProgressTracker(HoneyJarBackpack.ID).ifPresent(progressTracker ->
                {
                    CountProgressTracker tracker = (CountProgressTracker) progressTracker;
                    tracker.increment((ServerPlayerEntity) player);
                });
            });
        }
    }
}
