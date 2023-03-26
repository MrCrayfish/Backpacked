package com.mrcrayfish.backpacked.enchantment;

import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Author: MrCrayfish
 */
//@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
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

    //TODO reimplement
    /*@SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDropLoot(LivingDropsEvent event)
    {
        Entity entity = event.getSource().getEntity();
        if(!(entity instanceof ServerPlayer player))
            return;

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
    }*/
}
