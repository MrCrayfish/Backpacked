package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(Inventory.class)
public class PlayerInventoryMixin
{
    @Inject(method = "removeItem(Lnet/minecraft/world/item/ItemStack;)V", at = @At(value = "TAIL"))
    public void backpacked$RemoveItemTail(ItemStack stack, CallbackInfo ci)
    {
        Inventory playerInventory = (Inventory) (Object) this;
        BackpackedInventoryAccess access = (BackpackedInventoryAccess) playerInventory.player;
        for(int i = 0; i < access.backpacked$GetBackpackInventoryCount(); i++)
        {
            BackpackInventory inventory = access.backpacked$GetBackpackInventory(i);
            if(inventory != null)
            {
                for(int j = 0; j < inventory.getContainerSize(); j++)
                {
                    if(inventory.getItem(j) == stack)
                    {
                        inventory.setItem(j, ItemStack.EMPTY);
                        break;
                    }
                }
            }
        }
    }
}
