package com.mrcrayfish.backpacked.common.augment;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.common.augment.impl.LootboundAugment;
import com.mrcrayfish.backpacked.common.augment.impl.QuiverlinkAugment;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Predicate;

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

        Item originalItem = stack.getItem();
        FunnelResult result = funnelItemStackIntoBackpack(player, stack);

        // If at least one item was funnelled into the backpack, run vanilla calls
        int funnelCount = result.funnelCount();
        if(funnelCount > 0)
        {
            player.take(entity, funnelCount);
            player.awardStat(Stats.ITEM_PICKED_UP.get(originalItem), funnelCount);
            player.onItemPickup(entity);
        }

        // If the entire stack was funnelled, cancel further handling and discard the item entity
        if(!result.hasRemaining())
        {
            entity.discard();
            return true;
        }

        // Otherwise remaining stack will be put into inventory as normal
        return false;
    }

    /**
     * Handles the Funnelling augment when picking up arrows.
     *
     * @param player the player picking up the arrow
     * @param arrow  the arrow being picked up
     * @return True if further handing should be cancelled
     */
    public static boolean beforeArrowPickup(Player player, AbstractArrow arrow)
    {
        ItemStack stack = arrow.getPickupItemStackOrigin().copy();
        FunnelResult result = funnelItemStackIntoBackpack(player, stack);

        // If entire stack was funnelled, return true and prevent vanilla handling
        if(!result.hasRemaining())
            return true;

        // Rare case the stack is partially funnelled. Arrows in vanilla have a count of 1 but some mods might do weird stuff
        // Just try and add the remaining to inventory, don't care about if it was added or not
        if(result.funnelCount() > 0)
        {
            player.getInventory().add(stack);
            return true;
        }

        return false;
    }

    /**
     * Attempts to funnel the given stack into the backpack only if the backpack has the funnelling
     * augment. A result of the funnelling action will be returned after this method is called.
     * <p>
     * In the case no backpacks has the funnelling augment, a {@link FunnelResult#IGNORE} will be
     * returned; This means nothing happen and vanilla behaviour should run as normal. If the stack
     * was partially funnelled, for example only 2 of the 10 items were put into the backpacks,
     * {@link FunnelResult#hasRemaining} will be true and {@link FunnelResult#funnelCount} will be
     * assigned 2. If the entire stack was funnelled into the backpacks, {@link FunnelResult#hasRemaining}
     * will be false and {@link FunnelResult#funnelCount} will be equal to the count of the given
     * stack before it was funnelled into the backpacks.
     *
     * @param player the player which holds the backpacks the item should funnel into
     * @param stack  the stack to funnell into the backpacks
     * @return A result of the funnelling action (see docs above)
     */
    private static FunnelResult funnelItemStackIntoBackpack(Player player, ItemStack stack)
    {
        // Get backpacks with the Funnelling augment
        var snapshots = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.FUNNELLING.get());
        if(snapshots.isEmpty())
            return FunnelResult.IGNORE;

        // Iterate through the backpacks and attempt to funnel into their inventories
        int funnelCount = 0;
        for(var snapshot : snapshots)
        {
            if(snapshot.augment().test(stack))
            {
                int beforeCount = stack.getCount();
                ItemStack remaining = snapshot.inventory().addItem(stack);
                stack.setCount(remaining.getCount());
                funnelCount += (beforeCount - remaining.getCount());
                if(stack.isEmpty())
                {
                    break;
                }
            }
        }

        return new FunnelResult(!stack.isEmpty(), funnelCount);
    }

    private record FunnelResult(boolean hasRemaining, int funnelCount)
    {
        private static final FunnelResult IGNORE = new FunnelResult(false, 0);
    }

    public static ItemStack locateAmmunition(Player player, ItemStack weapon, ItemStack ammo)
    {
        if(!weapon.isEmpty() && weapon.getItem() instanceof ProjectileWeaponItem)
        {
            var snapshots = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.QUIVERLINK.get());
            for(var snapshot : snapshots)
            {
                if(!ammo.isEmpty() && snapshot.augment().priority() != QuiverlinkAugment.Priority.BACKPACK)
                    continue;

                BackpackInventory inventory = snapshot.inventory();
                Predicate<ItemStack> predicate = Services.PLATFORM.getValidProjectiles(weapon);
                ItemStack projectile = InventoryHelper.streamFor(inventory).filter(predicate).findFirst().orElse(ItemStack.EMPTY);
                if(!projectile.isEmpty())
                {
                    return projectile;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public static void onLootDroppedByEntity(Collection<ItemEntity> drops, Player player)
    {
        var snapshots = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.FUNNELLING.get(), ModAugmentTypes.LOOTBOUND.get());
        for(var snapshot : snapshots)
        {
            LootboundAugment augment = snapshot.secondAugment();
            if(!augment.entities())
                break;

            drops.removeIf(drop -> {
                ItemStack stack = drop.getItem().copy();
                FunnelResult result = funnelItemStackIntoBackpack(player, stack);
                if(result.hasRemaining()) {
                    drop.setItem(stack);
                }
                return stack.isEmpty();
            });
        }
    }

    public static void onLootDroppedByBlock(Collection<ItemEntity> drops, Player player)
    {
        var snapshots = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.FUNNELLING.get(), ModAugmentTypes.LOOTBOUND.get());
        for(var snapshot : snapshots)
        {
            LootboundAugment augment = snapshot.secondAugment();
            if(!augment.blocks())
                break;

            drops.removeIf(drop -> {
                ItemStack stack = drop.getItem().copy();
                FunnelResult result = funnelItemStackIntoBackpack(player, stack);
                if(result.hasRemaining()) {
                    drop.setItem(stack);
                }
                return stack.isEmpty();
            });
        }
    }
}
