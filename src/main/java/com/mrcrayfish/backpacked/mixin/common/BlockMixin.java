package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.Tags;
import com.mrcrayfish.backpacked.enchantment.FunnellingEnchantment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
@Mixin(Block.class)
public class BlockMixin
{
    @Inject(method = "dropResources(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getDrops(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;"),
            cancellable = true)
    private static void captureDrops(BlockState state, World world, BlockPos pos, @Nullable TileEntity tileEntity, Entity entity, ItemStack stack, CallbackInfo ci)
    {
        if(state.is(Tags.Blocks.FUNNELLING) && entity instanceof ServerPlayerEntity)
        {
            if(FunnellingEnchantment.onBreakBlock(state, (ServerWorld) world, pos, tileEntity, (ServerPlayerEntity) entity, stack))
            {
                ci.cancel();
            }
        }
    }
}
