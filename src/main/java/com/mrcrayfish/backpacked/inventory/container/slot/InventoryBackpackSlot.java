package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class InventoryBackpackSlot extends Slot
{
    public InventoryBackpackSlot(IInventory inventoryIn, int index, int xPosition, int yPosition)
    {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon()
    {
        return Pair.of(AtlasTexture.LOCATION_BLOCKS, Backpacked.EMPTY_BACKPACK_SLOT);
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return stack.getItem() instanceof BackpackItem;
    }

    @Override
    public boolean mayPickup(PlayerEntity player)
    {
        if(!Config.SERVER.lockBackpackIntoSlot.get())
            return true;
        CompoundNBT tag = this.getItem().getTag();
        return tag == null || tag.getList("Items", Constants.NBT.TAG_COMPOUND).isEmpty();
    }
}
