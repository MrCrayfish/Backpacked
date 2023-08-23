package com.mrcrayfish.backpacked.util;

import net.minecraft.nbt.CompoundTag;

/**
 * Author: MrCrayfish
 */
public interface Serializable
{
    CompoundTag serialize();

    void deserialize(CompoundTag tag);
}
