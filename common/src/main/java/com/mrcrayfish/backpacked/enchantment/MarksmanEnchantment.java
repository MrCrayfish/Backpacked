package com.mrcrayfish.backpacked.enchantment;

import com.mrcrayfish.backpacked.core.ModTags;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Author: MrCrayfish
 */
public class MarksmanEnchantment extends Enchantment
{
    public MarksmanEnchantment()
    {
        super(Enchantment.definition(ModTags.Items.BACKPACK_ENCHANTABLE, 1, 1, Enchantment.constantCost(25), Enchantment.constantCost(50), 8));
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }
}
