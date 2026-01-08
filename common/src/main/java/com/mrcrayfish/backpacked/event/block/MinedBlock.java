package com.mrcrayfish.backpacked.event.block;

import com.mrcrayfish.backpacked.common.BlockSnapshot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
@FunctionalInterface
public interface MinedBlock
{
    void handle(BlockSnapshot snapshot, ItemStack stack, ServerPlayer player);

    @FunctionalInterface
    interface CaptureTag
    {
        boolean handle(ServerPlayer player);
    }
}
