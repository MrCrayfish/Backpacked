package com.mrcrayfish.backpacked.platform.services;

import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public interface IPlatformHelper
{
    boolean testPredicate(Optional<BlockPredicate> optional, BlockState state, @Nullable CompoundTag tag);
}
