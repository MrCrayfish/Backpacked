package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.backpack.impl.RocketBackpack;
import com.mrcrayfish.backpacked.data.unlock.UnlockManager;
import com.mrcrayfish.backpacked.common.tracker.impl.CountProgressTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin
{
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 8))
    public void backpackedOnFallFlying(double dx, double dy, double dz, CallbackInfo ci)
    {
        Player player = (Player) (Object) this;
        if(!(player instanceof ServerPlayer))
            return;

        int distance = (int) Math.round(Math.sqrt(dx * dx + dy * dy + dz * dz));
        UnlockManager.getTracker(player).flatMap(tracker -> tracker.getProgressTracker(RocketBackpack.ID)).ifPresent(tracker -> {
            CountProgressTracker countTracker = (CountProgressTracker) tracker;
            countTracker.increment(distance, (ServerPlayer) player);
        });
    }
}
