package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.inventory.container.slot.InventoryBackpackSlot;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetHandlerMixin
{
    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleSetCreativeModeSlot", at = @At(value = "TAIL"))
    private void patchBackpackAction(ServerboundSetCreativeModeSlotPacket packetIn, CallbackInfo ci)
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
            this.player.inventoryMenu.getSlot(packetIn.getSlotNum()).set(stack);
            this.player.inventoryMenu.broadcastChanges();
        }
    }
}
