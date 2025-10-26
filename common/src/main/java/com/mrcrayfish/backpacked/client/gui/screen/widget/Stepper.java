package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class Stepper extends AbstractWidget
{
    private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/stepper_button"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/stepper_button_disabled"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/stepper_button_focused")
    );
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/stepper_background");
    private static final ResourceLocation INCREMENT_SPRITE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/stepper_increment");
    private static final ResourceLocation DECREMENT_SPRITE = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/stepper_decrement");

    private int value;
    private final Consumer<Integer> callback;
    private @Nullable Integer min;
    private @Nullable Integer max;

    public Stepper(int width, int height, int initialValue, Consumer<Integer> callback)
    {
        super(0, 0, width, height, CommonComponents.EMPTY);
        this.value = initialValue;
        this.callback = callback;
    }

    public Stepper setMin(int min)
    {
        this.min = min;
        return this;
    }

    public Stepper setMax(int max)
    {
        this.max = max;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        graphics.blitSprite(BACKGROUND_SPRITE, this.getX() + 5, this.getY(), this.getWidth() - 10, this.getHeight());

        boolean leftHovered = this.isDecrementHovered(mouseX, mouseY);
        graphics.blitSprite(BUTTON_SPRITES.get(true, leftHovered), this.getX(), this.getY(), this.getHeight(), this.getHeight());
        graphics.blitSprite(DECREMENT_SPRITE, this.getX() + (this.getHeight() - 4) / 2, this.getY() + (this.getHeight() - 6) / 2, 4, 6);

        boolean rightHovered = this.isIncrementHovered(mouseX, mouseY);
        graphics.blitSprite(BUTTON_SPRITES.get(true, rightHovered), this.getX() + this.getWidth() - this.getHeight(), this.getY(), this.getHeight(), this.getHeight());
        graphics.blitSprite(INCREMENT_SPRITE, this.getX() + this.getWidth() - this.getHeight() + (this.getHeight() - 4) / 2, this.getY() + (this.getHeight() - 6) / 2, 4, 6);

        graphics.drawCenteredString(Minecraft.getInstance().font, Integer.toString(this.value), this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 10) / 2 + 1, 0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    @Override
    protected boolean clicked(double mouseX, double mouseY)
    {
        return this.active && this.visible && (this.isDecrementHovered((int) mouseX, (int) mouseY) || this.isIncrementHovered((int) mouseX, (int) mouseY));
    }

    @Override
    public void onClick(double mouseX, double mouseY)
    {
        if(this.isDecrementHovered((int) mouseX, (int) mouseY))
        {
            this.adjustValue(-1);
            this.callback.accept(this.value);
        }
        else if(this.isIncrementHovered((int) mouseX, (int) mouseY))
        {
            this.adjustValue(1);
            this.callback.accept(this.value);
        }
    }

    private void adjustValue(int step)
    {
        long min = this.min != null ? this.min : Integer.MIN_VALUE;
        long max = this.max != null ? this.max : Integer.MAX_VALUE;
        long length = max - min + 1;
        long newValue = min + Math.floorMod(((long) this.value - min) + (long) step, length);
        this.value = (int) newValue;
    }

    private boolean isDecrementHovered(int mouseX, int mouseY)
    {
        return ScreenUtil.isPointInArea(mouseX, mouseY, this.getX(), this.getY(), this.getHeight(), this.getHeight());
    }

    private boolean isIncrementHovered(int mouseX, int mouseY)
    {
        return ScreenUtil.isPointInArea(mouseX, mouseY, this.getX() + this.getWidth() - this.getHeight(), this.getY(), this.getHeight(), this.getHeight());
    }
}
