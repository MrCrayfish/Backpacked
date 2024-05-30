package com.mrcrayfish.backpacked.enchantment;

import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Collection;

/**
 * Author: MrCrayfish
 */
public class LootedEnchantment extends Enchantment
{
    public LootedEnchantment()
    {
        super(Rarity.UNCOMMON, Services.BACKPACK.getEnchantmentCategory(), new EquipmentSlot[]{});
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }

    public static boolean onDropLoot(Collection<ItemEntity> drops, DamageSource source)
    {
        Entity entity = source.getEntity();
        if(!(entity instanceof ServerPlayer player))
            return false;

        ItemStack backpack = Services.BACKPACK.getBackpackStack(player);
        if(backpack.isEmpty())
            return false;

        if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LOOTED.get(), backpack) <= 0)
            return false;

        BackpackInventory inventory = ((BackpackedInventoryAccess) player).backpacked$GetBackpackInventory();
        if(inventory == null)
            return false;

        drops.forEach(itemEntity ->
        {
            ItemStack stack = itemEntity.getItem();
            ItemStack remaining = inventory.addItem(stack);
            if(!remaining.isEmpty())
            {
                itemEntity.setItem(remaining);
                player.level().addFreshEntity(itemEntity);
            }
        });
        return true;
    }
}
