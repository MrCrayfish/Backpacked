package com.mrcrayfish.backpacked.inventory;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import com.mrcrayfish.backpacked.util.PickpocketUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;

/**
 * Author: MrCrayfish
 */
public class BackpackInventory extends Inventory
{
    private final PlayerEntity player;
    private final ItemStack stack;

    public BackpackInventory(int cols, int rows, PlayerEntity player, ItemStack stack)
    {
        super(rows * cols);
        this.player = player;
        this.stack = stack;
        this.loadBackpackContents(player);
    }

    private void loadBackpackContents(PlayerEntity player)
    {
        CompoundNBT compound = this.stack.getOrCreateTag();
        if(compound.contains("Items", Constants.NBT.TAG_LIST))
        {
            InventoryHelper.loadAllItems(compound.getList("Items", Constants.NBT.TAG_COMPOUND), this, player.level, player.position());
        }
    }

    public ItemStack getBackpackStack()
    {
        return this.stack;
    }

    @Override
    public boolean stillValid(PlayerEntity player)
    {
        return Backpacked.getBackpackStack(this.player).equals(this.stack) && (this.player.equals(player) || PickpocketUtil.canPickpocketEntity(this.player, player));
    }

    @Override
    public void setChanged()
    {
        super.setChanged();
        CompoundNBT compound = this.stack.getOrCreateTag();
        compound.put("Items", InventoryHelper.saveAllItems(new ListNBT(), this));
    }
}
