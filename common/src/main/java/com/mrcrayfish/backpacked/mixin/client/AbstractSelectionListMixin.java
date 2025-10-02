package com.mrcrayfish.backpacked.mixin.client;

import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomSelectionList;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSelectionList.class)
public class AbstractSelectionListMixin
{
    @Shadow
    private boolean scrolling;

    @Inject(method = "getEntryAtPosition", at = @At(value = "HEAD"), cancellable = true)
    private void backpacked$GetEntry(double mouseX, double mouseY, CallbackInfoReturnable<Object> cir)
    {
        AbstractSelectionList<?> list = (AbstractSelectionList<?>) (Object) this;
        if(list instanceof CustomSelectionList<?> getter)
        {
            cir.setReturnValue(getter.getEntry(mouseX, mouseY));
        }
    }

    @Inject(method = "updateScrollingState", at = @At(value = "HEAD"), cancellable = true)
    private void backpacked$UpdateScroll(double mouseX, double mouseY, int button, CallbackInfo ci)
    {
        AbstractSelectionList<?> list = (AbstractSelectionList<?>) (Object) this;
        if(list instanceof CustomSelectionList<?> getter)
        {
            this.scrolling = getter.updateScroll(mouseX, mouseY, button);
            ci.cancel();
        }
    }

    @Inject(method = "mouseReleased", at = @At(value = "HEAD"))
    private void backpacked$UpdateScrollOnRelease(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir)
    {
        AbstractSelectionList<?> list = (AbstractSelectionList<?>) (Object) this;
        if(list instanceof CustomSelectionList<?>)
        {
            this.scrolling = false;
        }
    }
}
