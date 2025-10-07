package com.mrcrayfish.backpacked.common;

import com.google.common.collect.Iterators;
import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.augment.impl.FunnellingAugment;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author: MrCrayfish
 */
public class EnchantmentHandler
{
    public static void init()
    {
        PlayerEvents.PICKUP_EXPERIENCE.register(EnchantmentHandler::onPickupExperience);
    }

    public static boolean onBreakBlock(ServerPlayer player, List<ItemStack> drops)
    {
        /*List<Pair<BackpackInventory, FunnellingAugment>> list = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.FUNNELLING.get());
        if(list.isEmpty())
            return false;

        AtomicBoolean changed = new AtomicBoolean(false);
        drops.forEach(drop -> {
            if(drop.isEmpty())
                return;
            for(Pair<BackpackInventory, FunnellingAugment> pair : list) {
                BackpackInventory inventory = pair.getFirst();
                FunnellingAugment funnelling = pair.getSecond();
                if(funnelling.test(drop)) {
                    ItemStack remaining = inventory.addItem(drop);
                    changed.compareAndSet(false, drop.getCount() != remaining.getCount());
                    drop.setCount(remaining.getCount());
                    if(drop.isEmpty()) {
                        break;
                    }
                }
            }
        });
        return changed.get();*/
        return false;
    }

    public static boolean onDropLoot(Collection<ItemEntity> drops, DamageSource source)
    {
        Entity entity = source.getEntity();
        if(!(entity instanceof ServerPlayer player))
            return false;

        BackpackedInventoryAccess access = (BackpackedInventoryAccess) player;
        for(int i = 0; i < access.backpacked$GetBackpackInventoryCount(); i++)
        {
            BackpackInventory inventory = access.backpacked$GetBackpackInventory(i);
            if(inventory == null)
                continue;

            ItemStack backpack = inventory.getBackpackStack();
            HolderLookup<Enchantment> lookup = entity.level().holderLookup(Registries.ENCHANTMENT);
            if(EnchantmentHelper.getItemEnchantmentLevel(lookup.getOrThrow(ModEnchantments.LOOTED), backpack) <= 0)
                continue;

            drops.removeIf(drop -> {
                ItemStack stack = drop.getItem();
                ItemStack remaining = inventory.addItem(stack);
                if(remaining.isEmpty()) {
                    return true;
                }
                drop.setItem(remaining);
                return false;
            });
        }

        drops.forEach(player.level()::addFreshEntity);

        return true;
    }

    public static boolean onPickupExperience(Player player, ExperienceOrb orb)
    {
        if(!(player instanceof ServerPlayer serverPlayer))
            return false;

        if(orb.isRemoved())
            return false;

        HolderLookup<Enchantment> lookup = player.level().holderLookup(Registries.ENCHANTMENT);
        Holder.Reference<Enchantment> enchantment = lookup.getOrThrow(ModEnchantments.REPAIRMAN);

        BackpackedInventoryAccess access = (BackpackedInventoryAccess) player;
        for(int i = 0; i < access.backpacked$GetBackpackInventoryCount(); i++)
        {
            BackpackInventory inventory = access.backpacked$GetBackpackInventory(i);
            if(inventory == null)
                continue;

            ItemStack backpack = inventory.getBackpackStack();
            if(EnchantmentHelper.getItemEnchantmentLevel(enchantment, backpack) <= 0)
                continue;

            // TODO reconsider repairing every item
            InventoryHelper.streamFor(inventory).filter(stack -> {
                return !stack.isEmpty() && stack.isDamaged();
            }).forEach(stack -> {
                int repairableAmount = EnchantmentHelper.modifyDurabilityToRepairFromXp(serverPlayer.serverLevel(), stack, orb.getValue());
                int maxRepairableDamage = Math.min(repairableAmount, stack.getDamageValue());
                stack.setDamageValue(stack.getDamageValue() - maxRepairableDamage);
            });
        }

        return false;
    }
}
