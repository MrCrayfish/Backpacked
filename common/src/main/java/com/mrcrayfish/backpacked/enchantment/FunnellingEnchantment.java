package com.mrcrayfish.backpacked.enchantment;

import com.mrcrayfish.backpacked.core.ModEnchantments;
import com.mrcrayfish.backpacked.core.ModTags;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class FunnellingEnchantment extends Enchantment
{
    public FunnellingEnchantment()
    {
        super(Enchantment.definition(ModTags.Items.BACKPACK_ENCHANTABLE, 1, 1, Enchantment.constantCost(25), Enchantment.constantCost(50), 8));
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }

    public static boolean onBreakBlock(BlockState state, ServerLevel world, BlockPos pos, @Nullable BlockEntity blockEntity, ServerPlayer player, ItemStack stack)
    {
        ItemStack backpack = Services.BACKPACK.getBackpackStack(player);
        if(backpack.isEmpty())
            return false;

        if(EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FUNNELLING.get(), backpack) <= 0)
            return false;

        BackpackInventory inventory = ((BackpackedInventoryAccess) player).backpacked$GetBackpackInventory();
        if(inventory == null)
            return false;

        Block.getDrops(state, world, pos, blockEntity, player, stack).forEach((dropStack) -> {
            Block.popResource(world, pos, inventory.addItem(dropStack));
        });
        state.spawnAfterBreak(world, pos, stack, true);
        return true;
    }
}
