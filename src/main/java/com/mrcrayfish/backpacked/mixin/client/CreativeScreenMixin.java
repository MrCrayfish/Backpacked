package com.mrcrayfish.backpacked.mixin.client;

import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.mixin.common.SlotMixin;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Author: MrCrayfish
 */
@Mixin(CreativeScreen.class)
public class CreativeScreenMixin
{
    @Inject(method = "setCurrentCreativeTab", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 1))
    private void patchBackpackSlot(ItemGroup tab, CallbackInfo ci)
    {
        CreativeScreen screen = (CreativeScreen) (Object) this;
        screen.getContainer().inventorySlots.stream().filter(slot -> slot.inventory instanceof ExtendedPlayerInventory && slot.getSlotIndex() == 41).findFirst().ifPresent(slot -> {
            ((SlotMixin) slot).setXPos(127);
            ((SlotMixin) slot).setYPos(20);
        });
    }
}
