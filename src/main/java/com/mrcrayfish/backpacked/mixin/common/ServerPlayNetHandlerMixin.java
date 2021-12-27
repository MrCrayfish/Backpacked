package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.inventory.container.slot.InventoryBackpackSlot;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CCreativeInventoryActionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(ServerPlayNetHandler.class)
public class ServerPlayNetHandlerMixin
{
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "handleSetCreativeModeSlot", at = @At(value = "TAIL"))
    private void patchBackpackAction(CCreativeInventoryActionPacket packetIn, CallbackInfo ci)
    {
        if(!this.player.isCreative())
            return;

        ItemStack stack = packetIn.getItem();
        int maxSize = this.player.inventoryMenu.slots.size();
        if(packetIn.getSlotNum() <= 45 || packetIn.getSlotNum() >= maxSize)
            return;

        Slot slot = this.player.inventoryMenu.getSlot(packetIn.getSlotNum());
        if(!(slot instanceof InventoryBackpackSlot))
            return;

        boolean changed = stack.isEmpty() || stack.getDamageValue() >= 0 && stack.getCount() <= 64;
        if(changed)
        {
            if(stack.isEmpty())
            {
                this.player.inventoryMenu.setItem(packetIn.getSlotNum(), ItemStack.EMPTY);
            }
            else
            {
                this.player.inventoryMenu.setItem(packetIn.getSlotNum(), stack);
            }
            this.player.inventoryMenu.setSynched(this.player, true);
            this.player.inventoryMenu.broadcastChanges();
        }
    }
}
