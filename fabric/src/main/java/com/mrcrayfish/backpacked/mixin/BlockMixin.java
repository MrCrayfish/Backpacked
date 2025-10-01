package com.mrcrayfish.backpacked.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mrcrayfish.backpacked.common.EnchantmentHandler;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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
import java.util.List;
import java.util.stream.Collectors;

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
     * Second step, send out the event to modify the drops of the block but only if the breaker is
     * present. This can be achieved by modifying the list of item stacks returned by getDrops, and
     * returning a new list if something changed. The breaker is reset regardless if the event was sent.
     */
    @ModifyExpressionValue(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"))
    private static List<ItemStack> backpacked$modifyDrops(List<ItemStack> drops)
    {
        if(backpacked$breaker != null && backpacked$breaker.get() instanceof ServerPlayer player)
        {
            EnchantmentHandler.onBreakBlock(player, drops);
        }
        backpacked$breaker = null;
        return drops;
    }
}
