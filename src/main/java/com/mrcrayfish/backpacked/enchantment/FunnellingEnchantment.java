package com.mrcrayfish.backpacked.enchantment;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class FunnellingEnchantment extends Enchantment
{
    public FunnellingEnchantment()
    {
        super(Rarity.UNCOMMON, Backpacked.ENCHANTMENT_TYPE, new EquipmentSlot[]{});
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }

    public static boolean onBreakBlock(BlockState state, ServerLevel world, BlockPos pos, @Nullable BlockEntity blockEntity, ServerPlayer player, ItemStack stack)
    {
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(backpack.isEmpty())
            return false;

        if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FUNNELLING.get(), backpack) <= 0)
            return false;

        BackpackInventory inventory = ((BackpackedInventoryAccess) player).getBackpackedInventory();
        if(inventory == null)
            return false;

        Block.getDrops(state, world, pos, blockEntity, player, stack).forEach((dropStack) -> {
            Block.popResource(world, pos, inventory.addItem(dropStack));
        });
        state.spawnAfterBreak(world, pos, stack);
        return true;
    }
}
