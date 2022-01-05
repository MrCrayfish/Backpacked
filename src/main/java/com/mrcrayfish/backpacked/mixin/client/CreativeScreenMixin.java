package com.mrcrayfish.backpacked.mixin.client;

import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.mixin.common.SlotMixin;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(CreativeModeInventoryScreen.class)
public class CreativeScreenMixin
{
    @Inject(method = "selectTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/NonNullList;add(Ljava/lang/Object;)Z", ordinal = 3))
    private void patchBackpackSlot(CreativeModeTab tab, CallbackInfo ci)
    {
        CreativeModeInventoryScreen screen = (CreativeModeInventoryScreen) (Object) this;
        screen.getMenu().slots.stream().filter(slot -> slot.container instanceof ExtendedPlayerInventory && slot.getSlotIndex() == 41).findFirst().ifPresent(slot -> {
            ((SlotMixin) slot).setXPos(127);
            ((SlotMixin) slot).setYPos(20);
        });
    }
}
