package com.mrcrayfish.backpacked.mixin.common;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerRenderer.class)
public interface PlayerRendererInvoker
{
    @Invoker(value = "setupRotations")
    void backpacked$setupRotations(AbstractClientPlayer player, PoseStack poseStack, float scale, float bodyRot, float partialTick);
}
