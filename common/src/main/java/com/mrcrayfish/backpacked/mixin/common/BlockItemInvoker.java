package com.mrcrayfish.backpacked.mixin.common;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockItem.class)
public interface BlockItemInvoker
{
    @Invoker(value = "getPlacementState")
    BlockState backpacked$getPlacementState(BlockPlaceContext context);
}
