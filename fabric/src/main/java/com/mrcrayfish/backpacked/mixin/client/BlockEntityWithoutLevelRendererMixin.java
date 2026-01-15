package com.mrcrayfish.backpacked.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.client.renderer.BackpackItemSpecialRenderer;
import com.mrcrayfish.backpacked.core.ModItems;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public class BlockEntityWithoutLevelRendererMixin
{
    @Inject(method = "renderByItem", at = @At(value = "HEAD"), cancellable = true)
    private void backpacked$RenderBackpackModels(ItemStack stack, ItemDisplayContext context, PoseStack pose, MultiBufferSource source, int light, int overlay, CallbackInfo ci)
    {
        if(stack.is(ModItems.BACKPACK.get()))
        {
            BackpackItemSpecialRenderer.renderBackpack(stack, context, pose, source, light);
            ci.cancel();
        }
    }
}
