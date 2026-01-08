package com.mrcrayfish.backpacked.platform.services;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public interface IClientHelper
{
    void openConfigScreen();

    void invokeRotationSetup(AvatarRenderer<AbstractClientPlayer> renderer, AvatarRenderState state, PoseStack stack, float bodyRot, float scale);

    void drawTooltip(GuiGraphics graphics, Font font, List<ClientTooltipComponent> list, int mouseX, int mouseY, ClientTooltipPositioner positioner);

    void setMousePos(double x, double y);

    boolean isWearingBackpack(VillagerRenderState state);

    void submitGuiElementRenderState(GuiGraphics graphics, GuiElementRenderState state);
}
