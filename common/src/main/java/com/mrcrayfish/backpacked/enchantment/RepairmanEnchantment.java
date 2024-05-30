package com.mrcrayfish.backpacked.enchantment;

import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import com.mrcrayfish.framework.api.util.ItemStackHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;


public class RepairmanEnchantment extends Enchantment
{
    public RepairmanEnchantment()
    {
        super(Rarity.UNCOMMON, Services.BACKPACK.getEnchantmentCategory(), new EquipmentSlot[]{});
    }

    public static void init()
    {
        PlayerEvents.PICKUP_EXPERIENCE.register(RepairmanEnchantment::onPickupExperience);
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }

    private static boolean onPickupExperience(Player player, ExperienceOrb experienceOrb)
    {
        ItemStack backpack = Services.BACKPACK.getBackpackStack(player);
        if(backpack.isEmpty())
            return false;

        if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.REPAIRMAN.get(), backpack) <= 0)
            return false;

        BackpackInventory inventory = ((BackpackedInventoryAccess) player).backpacked$GetBackpackInventory();
        if(inventory == null)
            return false;

        if(experienceOrb.isRemoved())
            return false;

        InventoryHelper.streamFor(inventory).filter(stack -> {
            return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, stack) > 0 && stack.isDamaged();
        }).forEach(stack -> {
            int repaired = Math.min((int) ((experienceOrb.getValue() / 2) * ItemStackHelper.getRepairRatio(stack)), stack.getDamageValue());
            stack.setDamageValue(stack.getDamageValue() - repaired);
        });

        return false;
    }
}
