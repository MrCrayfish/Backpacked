package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.backpacked.util.ScreenUtil;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableInt;

public class ScrollBar extends AbstractWidget
{
    private static final WidgetSprites SCROLL_BAR_SPRITES = new WidgetSprites(
        Utils.rl("backpack/scroll_bar"),
        Utils.rl("backpack/scroll_bar_disabled")
    );

    private static final int SCROLL_BAR_WIDTH = 12;
    private static final int SCROLL_BAR_HEIGHT = 15;

    private final MutableInt scroll;
    private boolean grabbed;
    private int grabbedY;

    public ScrollBar(int x, int y, int height, MutableInt scroll)
    {
        super(x, y, SCROLL_BAR_WIDTH, height, CommonComponents.EMPTY);
        this.scroll = scroll;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {
        this.defaultButtonNarrationText(output);
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        int scroll = (this.active ? this.scroll.getValue() : 0);
        if(this.grabbed) scroll += mouseY - this.grabbedY;
        int scrollBarY = this.getY() + Mth.clamp(scroll, 0, this.getMaxScroll());
        graphics.blitSprite(SCROLL_BAR_SPRITES.get(this.active, false), this.getX(), scrollBarY, SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(!this.active || !this.visible || !this.isValidClickButton(button))
            return false;

        if(ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, this.getX(), this.getY() + this.scroll.getValue(), SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT))
        {
            if(!this.grabbed)
            {
                this.grabbed = true;
                this.grabbedY = (int) mouseY;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(this.grabbed)
        {
            if(this.isValidClickButton(button))
            {
                int newScroll = this.scroll.getValue() + (int) (mouseY - this.grabbedY);
                this.scroll.setValue(Mth.clamp(newScroll, 0, this.getMaxScroll()));
                this.grabbed = false;
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    private int getMaxScroll()
    {
        return this.getHeight() - SCROLL_BAR_HEIGHT;
    }

    /**
     * Gets the current scroll amount, which is a value between 0 and 1.
     *
     * @param mouseY the current mouse y position
     * @return a double value from 0 to 1
     */
    public double getScroll(int mouseY)
    {
        int maxScroll = this.getMaxScroll();
        int scroll = (this.active ? this.scroll.getValue() : 0);
        if(this.grabbed) scroll += mouseY - this.grabbedY;
        return maxScroll > 0 ? Mth.clamp(scroll, 0, maxScroll) / (double) maxScroll : 0;
    }

    /**
     * Scrolls the scroll bar to the given amount. The amount must be a value from 0 to 1. Smaller
     * or larger values will simply be clamped. If the given value is 0.5, this would indicate to
     * set the scroll bar to be half way scrolled.
     *
     * @param scrollAmount the amount to scroll to.
     */
    public void scrollTo(double scrollAmount)
    {
        int maxScroll = this.getMaxScroll();
        int newScroll = Mth.ceil(maxScroll * Math.clamp(scrollAmount, 0, 1));
        this.scroll.setValue(Mth.clamp(newScroll, 0, maxScroll));
    }

    /**
     * @return True if the scroll bar is currently being grabbed (aka user is using the scroll bar)
     */
    public boolean isGrabbed()
    {
        return this.grabbed;
    }
}
