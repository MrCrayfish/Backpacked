package com.mrcrayfish.backpacked.enchantment;

import com.mrcrayfish.backpacked.Backpacked;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.EquipmentSlotType;

/**
 * Author: MrCrayfish
 */
public class MarksmanEnchantment extends Enchantment
{
    public MarksmanEnchantment()
    {
        super(Rarity.UNCOMMON, Backpacked.ENCHANTMENT_TYPE, new EquipmentSlotType[]{});
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }
}
