package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.platform.services.IPlatformHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public class ForgePlatformHelper implements IPlatformHelper
{
    public static final CreativeModeTab TAB = new CreativeModeTab("backpacked.creative_tab")
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(ModItems.BACKPACK.get());
        }
    }.setEnchantmentCategories(Backpacked.ENCHANTMENT_TYPE);

    @Override
    public CreativeModeTab getCreativeModTab()
    {
        return TAB;
    }
}
