package com.mrcrayfish.backpacked.mixin.common;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IntegerProperty.class)
public interface IntegerPropertyMixin
{
    @Accessor("max")
    int backpacked$getMax();
}
