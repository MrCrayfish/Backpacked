package com.mrcrayfish.backpacked.event.block;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
@FunctionalInterface
public interface MinedBlock extends IFrameworkEvent
{
    void handle(BlockState state, ItemStack stack, Player player);
}
