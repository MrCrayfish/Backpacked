package com.mrcrayfish.backpacked.common.augment;

import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.common.augment.impl.FunnellingAugment;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AugmentHandler
{
    /**
     * Handles the Funnelling augment. When a player picks up an item entity, before adding the item
     * to the players inventory, any backpack that has the Funnelling augment will first attempt to
     * add the item to the backpack inventory, assuming the item also matches the user defined filters.
     *
     * @param player the player who is picking up the item
     * @param entity the item entity that is being picked up
     * @param target the target id of the item entity or null
     * @return Return true if vanilla handling should be cancelled
     */
    public static boolean beforeItemPickup(Player player, ItemEntity entity, @Nullable UUID target)
    {
        // Don't handle is item is empty
        ItemStack stack = entity.getItem();
        if(stack.isEmpty())
            return false;

        // Don't handle if a delay is present
        if(entity.hasPickUpDelay())
            return false;

        // Don't handle if the pickup target does not match the player
        if(target != null && !target.equals(player.getUUID()))
            return false;

        // Get backpacks with the Funnelling augment
        var pairs = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.FUNNELLING.get());
        if(pairs.isEmpty())
            return false;

        Item originalItem = stack.getItem();
        int funnelCount = 0;

        // Iterate through the backpacks and attempt to funnel into their inventories
        for(Pair<BackpackInventory, FunnellingAugment> pair : pairs)
        {
            BackpackInventory inventory = pair.getFirst();
            FunnellingAugment funnelling = pair.getSecond();
            if(funnelling.test(stack))
            {
                int beforeCount = stack.getCount();
                ItemStack remaining = inventory.addItem(stack);
                stack.setCount(remaining.getCount());
                funnelCount += (beforeCount - remaining.getCount());
                if(stack.isEmpty())
                    break;
            }
        }

        // If at least one item was funnelled into the backpack, run vanilla calls
        if(funnelCount > 0)
        {
            player.take(entity, funnelCount);
            player.awardStat(Stats.ITEM_PICKED_UP.get(originalItem), funnelCount);
            player.onItemPickup(entity);
        }

        // If all the items were funnelled, discard the entity and cancel further handling
        if(stack.isEmpty())
        {
            entity.discard();
            return true;
        }

        return false;
    }
}
