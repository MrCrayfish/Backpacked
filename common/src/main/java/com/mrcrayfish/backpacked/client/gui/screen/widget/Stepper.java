package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class Stepper extends AbstractWidget
{
    private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
        Utils.id("backpack/button_enabled"),
        Utils.id("backpack/button_disabled"),
        Utils.id("backpack/button_enabled_focused")
    );
    private static final Identifier BACKGROUND_SPRITE = Utils.id("backpack/stepper_background");
    private static final Identifier INCREMENT_SPRITE = Utils.id("backpack/stepper_increment");
    private static final Identifier DECREMENT_SPRITE = Utils.id("backpack/stepper_decrement");
    private static final IntRange DEFAULT_RANGE = new IntRange(Integer.MIN_VALUE, Integer.MAX_VALUE);

    private final IntRange range;
    private final @Nullable Consumer<Integer> callback;
    private final boolean wrap;
    private int value;

    private Stepper(int x, int y, int width, int height, IntRange range, @Nullable Consumer<Integer> callback, boolean wrap, int initialValue)
    {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.range = range;
        this.callback = callback;
        this.wrap = wrap;
        this.value = initialValue;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, this.getX() + this.getHeight(), this.getY() + 1, this.getWidth() - this.getHeight() * 2, this.getHeight() - 2);

        boolean leftHovered = this.isDecrementHovered(mouseX, mouseY);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON_SPRITES.get(true, leftHovered), this.getX(), this.getY(), this.getHeight(), this.getHeight());
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, DECREMENT_SPRITE, this.getX() + (this.getHeight() - 4) / 2, this.getY() + (this.getHeight() - 6) / 2, 4, 6);

        boolean rightHovered = this.isIncrementHovered(mouseX, mouseY);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BUTTON_SPRITES.get(true, rightHovered), this.getX() + this.getWidth() - this.getHeight(), this.getY(), this.getHeight(), this.getHeight());
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, INCREMENT_SPRITE, this.getX() + this.getWidth() - this.getHeight() + (this.getHeight() - 4) / 2, this.getY() + (this.getHeight() - 6) / 2, 4, 6);

        graphics.drawCenteredString(Minecraft.getInstance().font, Integer.toString(this.value), this.getX() + this.getWidth() / 2, this.getY() + (this.getHeight() - 10) / 2 + 1, 0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return (this.isDecrementHovered((int) mouseX, (int) mouseY) || this.isIncrementHovered((int) mouseX, (int) mouseY));
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo p_447020_)
    {
        return super.isValidClickButton(p_447020_);
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick)
    {
        if(this.isDecrementHovered((int) event.x(), (int) event.y()))
        {
            this.adjustValue(-1);
            if(this.callback != null)
            {
                this.callback.accept(this.value);
            }
        }
        else if(this.isIncrementHovered((int) event.x(), (int) event.y()))
        {
            this.adjustValue(1);
            if(this.callback != null)
            {
                this.callback.accept(this.value);
            }
        }
    }

    private void adjustValue(int step)
    {
        long min = this.range.min();
        long max = this.range.max();
        if(this.wrap)
        {
            long length = max - min + 1;
            long newValue = min + Math.floorMod(((long) this.value - min) + (long) step, length);
            this.value = (int) newValue;
        }
        else
        {
            long newValue = (long) this.value + step;
            this.value = (int) Mth.clamp(newValue, min, max);
        }
    }

    private boolean isDecrementHovered(int mouseX, int mouseY)
    {
        return ScreenUtil.isPointInArea(mouseX, mouseY, this.getX(), this.getY(), this.getHeight(), this.getHeight());
    }

    private boolean isIncrementHovered(int mouseX, int mouseY)
    {
        return ScreenUtil.isPointInArea(mouseX, mouseY, this.getX() + this.getWidth() - this.getHeight(), this.getY(), this.getHeight(), this.getHeight());
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private int x;
        private int y;
        private int width = 100;
        private int height = 20;
        private int initialValue;
        private IntRange range = DEFAULT_RANGE;
        private boolean wrap = false;
        private @Nullable Consumer<Integer> callback;

        private Builder() {}

        public Builder setPosition(int x, int y)
        {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder setSize(int width, int height)
        {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder setInitialValue(int initialValue)
        {
            this.initialValue = initialValue;
            return this;
        }

        public Builder setRange(int min, int max)
        {
            this.range = new IntRange(min, max);
            return this;
        }

        public Builder setWrap(boolean wrap)
        {
            this.wrap = wrap;
            return this;
        }

        public Builder setOnChange(Consumer<Integer> callback)
        {
            this.callback = callback;
            return this;
        }

        public Stepper build()
        {
            return new Stepper(this.x, this.y, this.width, this.height, this.range, this.callback, this.wrap, this.initialValue);
        }
    }

    private record IntRange(int min, int max) {}
}
