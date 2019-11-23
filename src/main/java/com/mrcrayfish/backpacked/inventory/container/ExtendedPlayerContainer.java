package com.mrcrayfish.backpacked.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class ExtendedPlayerContainer extends PlayerContainer
{
    public ExtendedPlayerContainer(PlayerInventory playerInventory, boolean localWorld, PlayerEntity playerIn)
    {
        super(playerInventory, localWorld, playerIn);
        this.addSlot(new Slot(playerInventory, 41, 77, 44)
        {
            @Nullable
            @OnlyIn(Dist.CLIENT)
            public String getSlotTexture()
            {
                return "backpacked:item/empty_backpack_slot";
            }
        });
    }
}
