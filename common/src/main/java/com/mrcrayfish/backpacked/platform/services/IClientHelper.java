package com.mrcrayfish.backpacked.platform.services;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

/**
 * Author: MrCrayfish
 */
public interface IClientHelper
{
    void openConfigScreen();

    void invokeRotationSetup(PlayerRenderer renderer, AbstractClientPlayer player, PoseStack stack, float scale, float bodyRot, float partialTick);
}
