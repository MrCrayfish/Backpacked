package com.mrcrayfish.backpacked.event.block;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
@FunctionalInterface
public interface InteractedWithBlock extends IFrameworkEvent
{
    void handle(BlockState original, ItemStack stack, @Nullable CompoundTag tag, ServerPlayer player);

    @FunctionalInterface
    interface CaptureTag extends IFrameworkEvent
    {
        boolean handle(BlockState original, ItemStack stack, ServerPlayer player);
    }
}
