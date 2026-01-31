package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import com.mrcrayfish.backpacked.common.challenge.impl.KillMobChallenge;
import com.mrcrayfish.backpacked.event.BackpackedInteractAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements BackpackedInteractAccess
{
    @Unique
    public List<ResourceLocation> backpacked$CapturedInteractIds = new ArrayList<>();

    @Override
    public List<ResourceLocation> getBackpacked$CapturedInteractIds()
    {
        return this.backpacked$CapturedInteractIds;
    }

    @Inject(method = "onChangedBlock", at = @At(value = "TAIL"))
    private void backpacked$TryAndPlaceTorch(BlockPos pos, CallbackInfo ci)
    {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if(player.isSpectator())
            return;
        ServerLevel level = player.serverLevel();
        int brightness = level.getMaxLocalRawBrightness(pos);
        AugmentHandler.onPlayerChangedBlockPos(player, level, pos, brightness);
    }

    @Inject(method = "die", at = @At(value = "TAIL"))
    private void backpacked$OnDeath(DamageSource source, CallbackInfo ci)
    {
        ServerPlayer player = (ServerPlayer) (Object) this;
        KillMobChallenge.Tracker.onLivingEntityDeath(player, source);
    }
}
