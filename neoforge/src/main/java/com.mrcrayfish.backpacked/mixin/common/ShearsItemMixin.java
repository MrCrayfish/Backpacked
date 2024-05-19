package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.backpack.impl.SheepPlushBackpack;
import com.mrcrayfish.backpacked.data.tracker.UnlockManager;
import com.mrcrayfish.backpacked.data.tracker.impl.CountProgressTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Author: MrCrayfish
 */
@Mixin(ShearsItem.class)
public class ShearsItemMixin
{
    @Inject(method = "interactLivingEntity", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/common/IShearable;onSheared(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;I)Ljava/util/List;"))
    public void backpackedTrackShearProgress(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir)
    {
        if(!(player instanceof ServerPlayer))
            return;

        UnlockManager.getTracker(player).flatMap(tracker -> tracker.getProgressTracker(SheepPlushBackpack.ID)).ifPresent(tracker -> {
            CountProgressTracker countTracker = (CountProgressTracker) tracker;
            countTracker.increment((ServerPlayer) player);
        });
    }
}
