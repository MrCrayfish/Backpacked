package com.mrcrayfish.backpacked.enchantment;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Reference;
import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
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
        super(Rarity.UNCOMMON, Backpacked.ENCHANTMENT_TYPE, new EquipmentSlot[]{});
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }

    @SubscribeEvent
    public static void onDropLoot(LivingDropsEvent event)
    {
        Entity entity = event.getSource().getEntity();
        if(!(entity instanceof ServerPlayer serverPlayer))
            return;

        ItemStack backpack = Backpacked.getBackpackStack(serverPlayer);
        if(backpack.isEmpty())
            return;

        if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LOOTED.get(), backpack) <= 0)
            return;

        event.setCanceled(true);

        BackpackInventory inventory = ((BackpackedInventoryAccess) serverPlayer).getBackpackedInventory();
        event.getDrops().forEach(itemEntity ->
        {
            ItemStack stack = itemEntity.getItem();
            ItemStack remaining = inventory.addItem(stack);
            if(!remaining.isEmpty())
            {
                itemEntity.setItem(remaining);
                serverPlayer.level.addFreshEntity(itemEntity);
            }
        });
    }
}
