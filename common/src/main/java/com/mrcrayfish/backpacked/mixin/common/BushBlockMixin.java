package com.mrcrayfish.backpacked.mixin.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BushBlock.class)
public interface BushBlockMixin
{
    @Invoker(value = "mayPlaceOn")
    boolean backpacked$mayPlaceOn(BlockState state, BlockGetter getter, BlockPos pos);
}
