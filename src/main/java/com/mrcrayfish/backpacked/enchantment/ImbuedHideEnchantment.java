package com.mrcrayfish.backpacked.enchantment;

import com.mrcrayfish.backpacked.Backpacked;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;

/**
 * Author: MrCrayfish
 */
public class ImbuedHideEnchantment extends Enchantment
{
    public ImbuedHideEnchantment()
    {
        super(Rarity.RARE, Backpacked.ENCHANTMENT_TYPE, new EquipmentSlotType[]{});
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }
}
