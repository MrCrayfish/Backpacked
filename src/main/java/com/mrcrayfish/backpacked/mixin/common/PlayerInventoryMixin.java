package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin
{
    @Inject(method = "removeItem(Lnet/minecraft/item/ItemStack;)V", at = @At(value = "TAIL"))
    public void removeItemTail(ItemStack stack, CallbackInfo ci)
    {
        PlayerInventory playerInventory = (PlayerInventory) (Object) this;
        PlayerEntity player = playerInventory.player;
        BackpackInventory inventory = ((BackpackedInventoryAccess) player).getBackpackedInventory();
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
