package com.mrcrayfish.backpacked.event.block;

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
    void handle(BlockState state, ItemStack stack, @Nullable CompoundTag tag, Player player);

    @FunctionalInterface
    interface CaptureTag extends IFrameworkEvent
    {
        boolean handle(BlockState state, ItemStack stack, Player player);
    }
}
