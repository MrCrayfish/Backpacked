package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.BackpackManager;
import com.mrcrayfish.backpacked.common.backpack.HoneyJarBackpack;
import com.mrcrayfish.backpacked.core.ModStats;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Author: MrCrayfish
 */
@Mixin(BeehiveBlock.class)
public class BeehiveBlockMixin
{
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;shrink(I)V"))
    public void onGatherHoneyBottle(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result, CallbackInfoReturnable<ActionResultType> cir)
    {
        player.awardStat(ModStats.GATHER_HONEY);
        if(player instanceof ServerPlayerEntity)
        {
            Stat<ResourceLocation> stat = Stats.CUSTOM.get(ModStats.GATHER_HONEY);
            int count = ((ServerPlayerEntity) player).getStats().getValue(stat);
            if(count >= 20)
            {
                BackpackManager.instance().unlockBackpack((ServerPlayerEntity) player, HoneyJarBackpack.ID);
            }
        }
    }
}
