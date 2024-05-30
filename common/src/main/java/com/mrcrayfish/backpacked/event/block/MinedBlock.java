package com.mrcrayfish.backpacked.event.block;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Author: MrCrayfish
 */
@FunctionalInterface
public interface MinedBlock extends IFrameworkEvent
{
    void handle(BlockState state, Player player);
}
