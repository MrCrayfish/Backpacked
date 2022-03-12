package com.mrcrayfish.backpacked.enchantment;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class LootedEnchantment extends Enchantment
{
    public LootedEnchantment()
    {
        super(Rarity.UNCOMMON, Backpacked.ENCHANTMENT_TYPE, new EquipmentSlotType[]{});
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDropLoot(LivingDropsEvent event)
    {
        Entity entity = event.getSource().getEntity();
        if(!(entity instanceof ServerPlayerEntity))
            return;

        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(backpack.isEmpty())
            return;

        if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LOOTED.get(), backpack) <= 0)
            return;

        BackpackInventory inventory = ((BackpackedInventoryAccess) player).getBackpackedInventory();
        if(inventory == null)
            return;

        event.setCanceled(true);

        event.getDrops().forEach(itemEntity ->
        {
            ItemStack stack = itemEntity.getItem();
            ItemStack remaining = inventory.addItem(stack);
            if(!remaining.isEmpty())
            {
                itemEntity.setItem(remaining);
                player.level.addFreshEntity(itemEntity);
            }
        });
    }
}
