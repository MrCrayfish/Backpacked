package com.mrcrayfish.backpacked.event.block;

import com.mrcrayfish.backpacked.common.BlockSnapshot;
import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
@FunctionalInterface
public interface MinedBlock extends IFrameworkEvent
{
    void handle(BlockSnapshot snapshot, ItemStack stack, ServerPlayer player);

    @FunctionalInterface
    interface CaptureTag extends IFrameworkEvent
    {
        boolean handle(ServerPlayer player);
    }
}
