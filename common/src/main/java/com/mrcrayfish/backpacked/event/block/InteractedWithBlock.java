package com.mrcrayfish.backpacked.event.block;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
@FunctionalInterface
public interface InteractedWithBlock extends IFrameworkEvent
{
    void handle(BlockState original, ItemStack stack, ServerPlayer player);
}
