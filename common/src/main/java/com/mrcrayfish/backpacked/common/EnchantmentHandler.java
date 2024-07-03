package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import net.minecraft.core.BlockPos;
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

/**
 * Author: MrCrayfish
 */
public class EnchantmentHandler
{
    public static void init()
    {
        PlayerEvents.PICKUP_EXPERIENCE.register(EnchantmentHandler::onPickupExperience);
    }

    public static boolean onBreakBlock(BlockState state, ServerLevel level, BlockPos pos, @Nullable BlockEntity blockEntity, ServerPlayer player, ItemStack stack)
    {
        ItemStack backpack = Services.BACKPACK.getBackpackStack(player);
        if(backpack.isEmpty())
            return false;

        HolderLookup<Enchantment> lookup = level.holderLookup(Registries.ENCHANTMENT);
        if(EnchantmentHelper.getItemEnchantmentLevel(lookup.getOrThrow(ModEnchantments.FUNNELLING), backpack) <= 0)
            return false;

        BackpackInventory inventory = ((BackpackedInventoryAccess) player).backpacked$GetBackpackInventory();
        if(inventory == null)
            return false;

        Block.getDrops(state, level, pos, blockEntity, player, stack).forEach((dropStack) -> {
            Block.popResource(level, pos, inventory.addItem(dropStack));
        });
        state.spawnAfterBreak(level, pos, stack, true);
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

        HolderLookup<Enchantment> lookup = entity.level().holderLookup(Registries.ENCHANTMENT);
        if(EnchantmentHelper.getItemEnchantmentLevel(lookup.getOrThrow(ModEnchantments.LOOTED), backpack) <= 0)
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

    public static boolean onPickupExperience(Player player, ExperienceOrb orb)
    {
        if(!(player instanceof ServerPlayer serverPlayer))
            return false;

        ItemStack backpack = Services.BACKPACK.getBackpackStack(player);
        if(backpack.isEmpty())
            return false;

        HolderLookup<Enchantment> lookup = player.level().holderLookup(Registries.ENCHANTMENT);
        if(EnchantmentHelper.getItemEnchantmentLevel(lookup.getOrThrow(ModEnchantments.REPAIRMAN), backpack) <= 0)
            return false;

        BackpackInventory inventory = ((BackpackedInventoryAccess) player).backpacked$GetBackpackInventory();
        if(inventory == null)
            return false;

        if(orb.isRemoved())
            return false;

        InventoryHelper.streamFor(inventory).filter(stack -> {
            return stack.isDamaged() && EnchantmentHelper.has(stack, EnchantmentEffectComponents.REPAIR_WITH_XP);
        }).forEach(stack -> {
            int repairableAmount = EnchantmentHelper.modifyDurabilityToRepairFromXp(serverPlayer.serverLevel(), stack, orb.getValue());
            int maxRepairableDamage = Math.min(repairableAmount, stack.getDamageValue());
            stack.setDamageValue(stack.getDamageValue() - maxRepairableDamage);
        });

        return false;
    }
}
