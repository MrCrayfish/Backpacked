package com.mrcrayfish.backpacked.mixin.common;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Author: MrCrayfish
 */
@Mixin(Slot.class)
public interface SlotMixin
{
    @Mutable
    @Accessor(value = "x")
    void setXPos(int x);

    @Mutable
    @Accessor(value = "y")
    void setYPos(int y);
}
