package com.mrcrayfish.backpacked.inventory.container.slot;

import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    public Pair<ResourceLocation, ResourceLocation> getBackground()
    {
        return Pair.of(AtlasTexture.LOCATION_BLOCKS_TEXTURE, Backpacked.EMPTY_BACKPACK_SLOT);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return stack.getItem() instanceof BackpackItem;
    }
}
