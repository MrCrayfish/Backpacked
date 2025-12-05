package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.MovementType;
import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import com.mrcrayfish.backpacked.event.BackpackedEvents;
import com.mrcrayfish.backpacked.event.BackpackedInteractAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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

    @Unique
    private void backpacked$PlayerTravelEvent(double dx, double dy, double dz, MovementType type)
    {
        Player player = (Player) (Object) this;
        if(player.level().isClientSide())
            return;
        double distanceSquared = dx * dx + dy * dy + dz * dz;
        BackpackedEvents.PLAYER_TRAVEL.post().handle((ServerPlayer) player, distanceSquared, type);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 0))
    private void backpacked$MovementSwim(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, dy, dz, MovementType.SWIM);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 1))
    private void backpacked$MovementWalkUnderwater(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, dy, dz, MovementType.WALK_UNDERWATER);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 2))
    private void backpacked$MovementWalkOnWater(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, 0, dz, MovementType.WALK_ON_WATER);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 3))
    private void backpacked$MovementClimb(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(0, dy, 0, MovementType.CLIMB);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 4))
    private void backpacked$MovementSprint(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, 0, dz, MovementType.SPRINT);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 5))
    private void backpacked$MovementSneak(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, 0, dz, MovementType.SNEAK);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 6))
    private void backpacked$MovementWalk(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, 0, dz, MovementType.WALK);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 7))
    private void backpacked$MovementElytraFlying(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, dy, dz, MovementType.ELYTRA_FLY);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 8))
    private void backpacked$MovementFlying(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, 0, dz, MovementType.FLY);
    }

    @Inject(method = "checkMovementStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/resources/ResourceLocation;I)V", ordinal = 8))
    private void backpacked$MovementFall(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(0, dy, 0, MovementType.FALL);
    }

    @Inject(method = "checkRidingStatistics", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getVehicle()Lnet/minecraft/world/entity/Entity;", ordinal = 0))
    private void backpacked$MovementVehicle(double dx, double dy, double dz, CallbackInfo ci)
    {
        this.backpacked$PlayerTravelEvent(dx, dy, dz, MovementType.VEHICLE);
    }

    @Inject(method = "onChangedBlock", at = @At(value = "TAIL"))
    private void backpacked$TryAndPlaceTorch(ServerLevel level, BlockPos pos, CallbackInfo ci)
    {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if(player.isSpectator())
            return;
        int brightness = level.getMaxLocalRawBrightness(pos);
        AugmentHandler.onPlayerChangedBlockPos(player, level, pos, brightness);
    }
}
