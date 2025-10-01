package com.mrcrayfish.backpacked.mixin.client;

import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomSelectionList;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSelectionList.class)
public class AbstractSelectionListMixin
{
    @Inject(method = "getEntryAtPosition", at = @At(value = "HEAD"), cancellable = true)
    private void backpacked$GetEntry(double mouseX, double mouseY, CallbackInfoReturnable<Object> cir)
    {
        AbstractSelectionList<?> list = (AbstractSelectionList<?>) (Object) this;
        if(list instanceof CustomSelectionList<?> getter)
        {
            cir.setReturnValue(getter.getEntry(mouseX, mouseY));
        }
    }
}
