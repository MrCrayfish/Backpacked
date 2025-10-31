package com.mrcrayfish.backpacked.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
@Mixin(Block.class)
public class BlockMixin
{
    @Unique
    @Nullable
    private static WeakReference<ServerPlayer> backpacked$breaker;

    /*
     * First step, capture the breaker as a reference before a block is about to drop its resources. The
     * breaker is only captured if a server player. This later signals that the drops are allowed to
     * be modified and an event will be called.
     */
    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
    private static void backpacked$captureBreaker(BlockState state, Level level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo ci)
    {
        if(entity instanceof ServerPlayer player)
        {
            backpacked$breaker = new WeakReference<>(player);
        }
    }

    /*
     * Second step, send out the event to the augment handler but only if the breaker is present. Due
     * to Fabric not having a loot event, to ensure the best compatibility with other mods, each
     * item entity is processed just before it is dropped, rather than collecting, performing logic,
     * then dropping (like the event in NeoForge would do). This is fine because most blocks only
     * drop one item but will add more overhead compared to when using NeoForge.
     */
    @Inject(method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDefaultPickUpDelay()V"),
            cancellable = true
    )
    private static void backpacked$onPopResource(Level level, Supplier<ItemEntity> supplier, ItemStack stack, CallbackInfo ci, @Local(ordinal = 0) ItemEntity entity)
    {
        if(backpacked$breaker != null && backpacked$breaker.get() instanceof ServerPlayer player)
        {
            List<ItemEntity> drops = new ArrayList<>(1);
            drops.add(entity);
            AugmentHandler.onLootDroppedByBlock(drops, player);
            if(drops.isEmpty())
            {
                ci.cancel();
            }
        }
    }

    /*
     * Third step, after all the dropped item entities have been processed, reset the breaker to
     * prevent the custom handling (in the above mixin).
     */
    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;spawnAfterBreak(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;Z)V"))
    private static void backpacked$afterCaptureBreaker(BlockState blockState, Level level, BlockPos blockPos, BlockEntity blockEntity, Entity entity, ItemStack itemStack, CallbackInfo ci)
    {
        backpacked$breaker = null;
    }
}
