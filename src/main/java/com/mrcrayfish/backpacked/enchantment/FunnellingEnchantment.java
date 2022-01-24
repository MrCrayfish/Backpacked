package com.mrcrayfish.backpacked.enchantment;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class FunnellingEnchantment extends Enchantment
{
    public FunnellingEnchantment()
    {
        super(Rarity.UNCOMMON, Backpacked.ENCHANTMENT_TYPE, new EquipmentSlotType[]{});
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }

    public static boolean onBreakBlock(BlockState state, ServerWorld world, BlockPos pos, @Nullable TileEntity tileEntity, ServerPlayerEntity player, ItemStack stack)
    {
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(backpack.isEmpty())
            return false;

        if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FUNNELLING.get(), backpack) <= 0)
            return false;

        BackpackInventory inventory = ((BackpackedInventoryAccess) player).getBackpackedInventory();
        Block.getDrops(state, world, pos, tileEntity, player, stack).forEach((dropStack) -> {
            Block.popResource(world, pos, inventory.addItem(dropStack));
        });
        state.spawnAfterBreak(world, pos, stack);
        return true;
    }
}
