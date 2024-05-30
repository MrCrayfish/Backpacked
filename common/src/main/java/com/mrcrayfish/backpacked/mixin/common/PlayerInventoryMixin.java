package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
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
        Player player = playerInventory.player;
        BackpackInventory inventory = ((BackpackedInventoryAccess) player).backpacked$GetBackpackInventory();
        if(inventory == null)
            return;

        for(int i = 0; i < inventory.getContainerSize(); i++)
        {
            if(inventory.getItem(i) == stack)
            {
                inventory.setItem(i, ItemStack.EMPTY);
                break;
            }
        }
    }
}
