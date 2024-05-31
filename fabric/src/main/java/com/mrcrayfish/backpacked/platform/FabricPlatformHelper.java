package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.platform.services.IPlatformHelper;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class FabricPlatformHelper implements IPlatformHelper
{
    @Override
    public boolean testPredicate(Optional<BlockPredicate> optional, BlockState state, @Nullable CompoundTag tag)
    {
        if(optional.isEmpty())
            return true;
        BlockPredicate predicate = optional.get();
        if(predicate.tag != null && !state.is(predicate.tag))
            return false;
        if(predicate.blocks != null && !predicate.blocks.contains(state.getBlock()))
            return false;
        if(tag != null && predicate.nbt.matches(tag)) {
            return false;
        }
        return predicate.properties.matches(state);
    }
}
