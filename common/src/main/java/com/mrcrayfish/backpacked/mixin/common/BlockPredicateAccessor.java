package com.mrcrayfish.backpacked.mixin.common;

import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockPredicate.class)
public interface BlockPredicateAccessor
{
    @Accessor("nbt")
    NbtPredicate backpacked$nbt();
}
