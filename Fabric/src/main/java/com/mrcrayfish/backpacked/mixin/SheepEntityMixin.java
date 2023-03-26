package com.mrcrayfish.backpacked.mixin;

import com.mrcrayfish.backpacked.common.backpack.impl.SheepPlushBackpack;
import com.mrcrayfish.backpacked.data.tracker.UnlockManager;
import com.mrcrayfish.backpacked.data.tracker.impl.CountProgressTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
    @Inject(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Sheep;shear(Lnet/minecraft/sounds/SoundSource;)V"), remap = false)
    public void trackShearProgress(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir)
    {
        if(!(player instanceof ServerPlayer))
            return;

        UnlockManager.get(player).flatMap(tracker -> tracker.getProgressTracker(SheepPlushBackpack.ID)).ifPresent(tracker -> {
            CountProgressTracker countTracker = (CountProgressTracker) tracker;
            countTracker.increment((ServerPlayer) player);
        });
    }
}
