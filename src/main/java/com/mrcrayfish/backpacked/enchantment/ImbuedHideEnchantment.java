package com.mrcrayfish.backpacked.enchantment;

import com.mrcrayfish.backpacked.Backpacked;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Author: MrCrayfish
 */
public class ImbuedHideEnchantment extends Enchantment
{
    public ImbuedHideEnchantment()
    {
        super(Rarity.RARE, Backpacked.ENCHANTMENT_TYPE, new EquipmentSlot[]{});
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }
}
