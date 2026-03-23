package com.mrcrayfish.backpacked.platform.services;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.client.renderer.entity.state.BackpackRenderState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public interface IClientHelper
{
    void openConfigScreen();

    void invokeRotationSetup(AvatarRenderer<AbstractClientPlayer> renderer, AvatarRenderState state, PoseStack stack, float bodyRot, float scale);

    void drawTooltip(GuiGraphicsExtractor extractor, Font font, List<ClientTooltipComponent> list, int mouseX, int mouseY, ClientTooltipPositioner positioner);

    void setMousePos(double x, double y);

    void submitGuiElementRenderState(GuiGraphicsExtractor extractor, GuiElementRenderState state);

    void submitGuiPipRenderState(GuiGraphicsExtractor extractor, PictureInPictureRenderState state);

    @Nullable
    BackpackRenderState getBackpackRenderState(EntityRenderState state);
}
