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

    @Inject(method = "processCreativeInventoryAction", at = @At(value = "TAIL"))
    private void patchBackpackAction(CCreativeInventoryActionPacket packetIn, CallbackInfo ci)
    {
        if(!this.player.isCreative())
            return;

        ItemStack stack = packetIn.getStack();
        int maxSize = this.player.container.inventorySlots.size();
        if(packetIn.getSlotId() <= 45 || packetIn.getSlotId() >= maxSize)
            return;

        Slot slot = this.player.container.getSlot(packetIn.getSlotId());
        if(!(slot instanceof InventoryBackpackSlot))
            return;

        boolean changed = stack.isEmpty() || stack.getDamage() >= 0 && stack.getCount() <= 64;
        if(changed)
        {
            if(stack.isEmpty())
            {
                this.player.container.putStackInSlot(packetIn.getSlotId(), ItemStack.EMPTY);
            }
            else
            {
                this.player.container.putStackInSlot(packetIn.getSlotId(), stack);
            }
            this.player.container.setCanCraft(this.player, true);
            this.player.container.detectAndSendChanges();
        }
    }
}
