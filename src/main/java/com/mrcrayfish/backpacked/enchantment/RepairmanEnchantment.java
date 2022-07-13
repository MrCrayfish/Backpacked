package com.mrcrayfish.backpacked.enchantment;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class RepairmanEnchantment extends Enchantment
{
    public RepairmanEnchantment()
    {
        super(Rarity.UNCOMMON, Backpacked.ENCHANTMENT_TYPE, new EquipmentSlot[]{});
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPickupExperience(PlayerXpEvent.PickupXp event)
    {
        Player player = event.getEntity();
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(backpack.isEmpty())
            return;

        if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.REPAIRMAN.get(), backpack) <= 0)
            return;

        BackpackInventory inventory = ((BackpackedInventoryAccess) player).getBackpackedInventory();
        if(inventory == null)
            return;

        ExperienceOrb orb = event.getOrb();
        if(orb.isRemoved())
            return;

        InventoryHelper.streamFor(inventory).filter(stack -> {
            return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, stack) > 0 && stack.isDamaged();
        }).forEach(stack -> {
            int repaired = Math.min((int) ((orb.value / 2) * stack.getXpRepairRatio()), stack.getDamageValue());
            stack.setDamageValue(stack.getDamageValue() - repaired);
        });
    }
}
