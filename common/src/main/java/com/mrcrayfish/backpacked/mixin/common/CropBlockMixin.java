package com.mrcrayfish.backpacked.mixin.common;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CropBlock.class)
public interface CropBlockMixin
{
    @Invoker(value = "getBaseSeedId")
    ItemLike backpacked$getBaseSeedId();
}
