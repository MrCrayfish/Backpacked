package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.backpack.impl.SheepPlushBackpack;
import com.mrcrayfish.backpacked.data.tracker.UnlockManager;
import com.mrcrayfish.backpacked.data.tracker.impl.CountProgressTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Author: MrCrayfish
 */
@Mixin(Sheep.class)
public class SheepEntityMixin
{
    @Inject(method = "onSheared", at = @At(value = "HEAD"), remap = false)
    public void backpackedTrackShearProgress(Player player, ItemStack item, Level world, BlockPos pos, int fortune, CallbackInfoReturnable<List<ItemStack>> cir)
    {
        if(!(player instanceof ServerPlayer))
            return;

        UnlockManager.getTracker(player).flatMap(tracker -> tracker.getProgressTracker(SheepPlushBackpack.ID)).ifPresent(tracker -> {
            CountProgressTracker countTracker = (CountProgressTracker) tracker;
            countTracker.increment((ServerPlayer) player);
        });
    }
}
