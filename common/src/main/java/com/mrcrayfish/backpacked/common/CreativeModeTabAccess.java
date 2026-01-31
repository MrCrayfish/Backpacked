package com.mrcrayfish.backpacked.common;

import net.minecraft.world.item.CreativeModeTab;

public interface CreativeModeTabAccess
{
    /**
     * Gives access to the display items generator. The generator determines the items in the
     * creative tab and the order they are viewed.
     *
     * @return The DisplayItemsGenerator of this CreativeModeTab
     */
    CreativeModeTab.DisplayItemsGenerator backpacked$displayItemsGenerator();
}
