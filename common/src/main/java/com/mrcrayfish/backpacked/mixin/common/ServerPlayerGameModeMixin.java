package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.BlockSnapshot;
import com.mrcrayfish.backpacked.event.BackpackedEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * This mixin captures and posts an event when a block is mined by a player. This mixin has been
 * designed to in a way to support all modloaders. Forge/NeoForge have patches on some of the target
 * methods, which makes it hard to target. The implementation to detect to blocks being mined is
 * spread across different methods and sections to avoid targeting issues.
 * <p>
 * See {@link BackpackedEvents#MINED_BLOCK} for relevant event
 * <p>
 * Author: MrCrayfish
 */
@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin // TODO DONE
{
    /*******************************
     * Capture mining block events *
     *******************************/

    @Shadow
    @Final
    protected ServerPlayer player;

    @Unique
    private BlockSnapshot backpacked$capturedMinedBlock;

    @Unique
    private ItemStack backpacked$capturedMinedItem;

    @Inject(method = "destroyBlock", at = @At(
        value = "INVOKE_ASSIGN",
        target = "Lnet/minecraft/server/level/ServerLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
        ordinal = 0,
        shift = At.Shift.BY, by = 1),
        locals = LocalCapture.CAPTURE_FAILHARD)
    private void backpacked$OnBlockMined(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState state)
    {
        this.backpacked$capturedMinedItem = this.player.getMainHandItem();
        if(BackpackedEvents.MINED_BLOCK_CAPTURE_TAG.post().handle(this.player))
        {
            this.backpacked$capturedMinedBlock = BlockSnapshot.captureWithTag(this.player.serverLevel(), pos);
        }
        else
        {
            this.backpacked$capturedMinedBlock = BlockSnapshot.capture(this.player.serverLevel(), pos);
        }
    }

    @Inject(method = "destroyAndAck", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/server/level/ServerPlayerGameMode;debugLogging(Lnet/minecraft/core/BlockPos;ZILjava/lang/String;)V",
        ordinal = 0))
    private void backpacked$AfterSuccessfulDestroy(BlockPos pos, int action, String message, CallbackInfo ci)
    {
        if(this.backpacked$capturedMinedBlock != null && this.backpacked$capturedMinedItem != null)
        {
            BackpackedEvents.MINED_BLOCK.post().handle(this.backpacked$capturedMinedBlock, this.backpacked$capturedMinedItem, this.player);
        }
    }

    @Inject(method = "destroyAndAck", at = @At(value = "TAIL"))
    private void backpacked$DestroyTail(BlockPos pos, int action, String message, CallbackInfo ci)
    {
        this.backpacked$capturedMinedBlock = null;
        this.backpacked$capturedMinedItem = null;
    }

    /***************************
     * Use item on block event *
     ***************************/

    @Unique
    private BlockSnapshot backpacked$capturedUseBlockSnapshot;

    @Unique
    private ItemStack backpacked$capturedUseItem;

    @Inject(method = "useItemOn", at = @At(value = "HEAD"))
    private void backpacked$OnUse(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult result, CallbackInfoReturnable<InteractionResult> cir)
    {
        this.backpacked$capturedUseItem = stack.copy();
        if(BackpackedEvents.INTERACTED_WITH_BLOCK_CAPTURE_TAG.post().handle(player))
        {
            this.backpacked$capturedUseBlockSnapshot = BlockSnapshot.captureWithTag(player.serverLevel(), result.getBlockPos());
        }
        else
        {
            this.backpacked$capturedUseBlockSnapshot = BlockSnapshot.capture(player.serverLevel(), result.getBlockPos());
        }
    }

    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/ItemUsedOnLocationTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private void backpacked$BeforeTriggers(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult result, CallbackInfoReturnable<InteractionResult> cir)
    {
        if(this.backpacked$capturedUseBlockSnapshot != null && this.backpacked$capturedUseItem != null)
        {
            BackpackedEvents.INTERACTED_WITH_BLOCK.post().handle(this.backpacked$capturedUseBlockSnapshot, this.backpacked$capturedUseItem, player);
            this.backpacked$capturedUseBlockSnapshot = null;
            this.backpacked$capturedUseItem = null;
        }
    }
}
