package com.mrcrayfish.backpacked.client.gui.pip;

import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public record GuiBackpackRenderState(
        @Nullable ItemTransform transform,
        @Nullable BackpackRenderer renderer,
        @Nullable LivingEntityData entityData,
        Identifier baseModel,
        Identifier strapsModel,
        int tickCount,
        float partialTick,
        int x0,
        int x1,
        int y0,
        int y1,
        float scale,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState
{
    public GuiBackpackRenderState(@Nullable ItemTransform transform, @Nullable BackpackRenderer renderer, @Nullable LivingEntityData entityData, Identifier baseModel, Identifier strapsModel, int tickCount, float partialTick, int x0, int x1, int y0, int y1, float scale, @Nullable ScreenRectangle scissorArea)
    {
        this(transform, renderer, entityData, baseModel, strapsModel, tickCount, partialTick, x0, x1, y0, y1, scale, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
    }
}