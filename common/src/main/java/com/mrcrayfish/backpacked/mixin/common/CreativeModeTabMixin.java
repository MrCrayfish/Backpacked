package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.CreativeModeTabAccess;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin implements CreativeModeTabAccess
{
    @Shadow
    @Final
    private CreativeModeTab.DisplayItemsGenerator displayItemsGenerator;

    @Override
    public CreativeModeTab.DisplayItemsGenerator backpacked$displayItemsGenerator()
    {
        return this.displayItemsGenerator;
    }
}
