package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.backpacked.client.gui.StateSprites;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class CustomSelectionList<E extends ObjectSelectionList.Entry<E>> extends ObjectSelectionList<E>
{
    private static final int OUTLINE_SIZE = 1;
    private static final int SCROLL_BAR_GAP = 2;

    protected int contentPadding = 2;
    protected int scrollBarWidth = 6;
    protected int itemSpacing = 2;
    protected boolean scrolling;
    protected boolean scrollBarAlwaysVisible;
    protected @Nullable ResourceLocation listBackground;
    protected @Nullable StateSprites itemSprites;
    protected @Nullable StateSprites scrollBarSprites;
    protected @Nullable ResourceLocation scrollBarBackground;
    protected ScrollBarStyle scrollBarStyle = ScrollBarStyle.DETACHED;

    public CustomSelectionList(int width, int height, int x, int y, int itemHeight)
    {
        super(Minecraft.getInstance(), width, height, y, itemHeight);
        this.setPosition(x, y);
    }

    public void setPosition(int x, int y)
    {
        super.setPosition(x, y);
        this.setSize(this.width, this.height);
    }

    public void setContentPadding(int contentPadding)
    {
        this.contentPadding = contentPadding;
    }

    public void setScrollBarWidth(int scrollBarWidth)
    {
        this.scrollBarWidth = scrollBarWidth;
    }

    public void setItemSpacing(int itemSpacing)
    {
        this.itemSpacing = itemSpacing;
    }

    public void setListBackground(@Nullable ResourceLocation background)
    {
        this.listBackground = background;
    }

    public void setItemSprites(@Nullable StateSprites sprites)
    {
        this.itemSprites = sprites;
    }

    public void setScrollBarSprites(@Nullable StateSprites sprites)
    {
        this.scrollBarSprites = sprites;
    }

    public void setScrollBarBackground(@Nullable ResourceLocation background)
    {
        this.scrollBarBackground = background;
    }

    public void setScrollBarStyle(ScrollBarStyle scrollBarStyle)
    {
        this.scrollBarStyle = scrollBarStyle;
    }

    public void setScrollBarAlwaysVisible(boolean scrollBarAlwaysVisible)
    {
        this.scrollBarAlwaysVisible = scrollBarAlwaysVisible;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight() && mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth();
    }

    @Override
    public int getRowWidth()
    {
        return this.getRowRight() - this.getRowLeft();
    }

    @Override
    public int getRowLeft()
    {
        return this.getX() + OUTLINE_SIZE + this.contentPadding;
    }

    @Override
    public int getRowRight()
    {
        if(this.getMaxScroll() > 0 || this.scrollBarAlwaysVisible)
        {
            int scrollBarArea = switch(this.scrollBarStyle) {
                case DETACHED -> this.contentPadding + OUTLINE_SIZE + SCROLL_BAR_GAP + OUTLINE_SIZE + this.contentPadding + this.scrollBarWidth + this.contentPadding + OUTLINE_SIZE;
                case MERGED -> this.contentPadding + this.scrollBarWidth + this.contentPadding + OUTLINE_SIZE;
            };
            return this.getX() + this.getWidth() - scrollBarArea;
        }
        return this.getX() + this.getWidth() - this.contentPadding - OUTLINE_SIZE;
    }

    @Override
    protected int getRowTop(int index)
    {
        return this.getY() + OUTLINE_SIZE + this.contentPadding - (int) this.getScrollAmount() + index * this.itemHeight + index * this.itemSpacing;
    }

    @Override
    protected int getScrollbarPosition()
    {
        return this.getX() + this.getWidth() - this.scrollBarWidth - this.contentPadding - OUTLINE_SIZE;
    }

    private int getScrollbarHeight()
    {
        int scrollAreaHeight = this.getScrollAreaHeight();
        int scrollBarHeight = (int) (Mth.square(scrollAreaHeight) / (float) this.getMaxPosition());
        return Mth.clamp(scrollBarHeight, 32, scrollAreaHeight);
    }

    public int getScrollBottom()
    {
        return (int) this.getScrollAmount() - this.height;
    }

    public int getScrollAreaHeight()
    {
        return this.height - OUTLINE_SIZE * 2 - this.contentPadding * 2;
    }

    public int getScrollAreaTop()
    {
        return this.getY() + OUTLINE_SIZE + this.contentPadding;
    }

    @Override
    public int getMaxScroll()
    {
        return Math.max(0, this.getMaxPosition() - this.height + this.contentPadding * 2 + OUTLINE_SIZE * 2);
    }

    @Override
    protected int getMaxPosition()
    {
        return this.getItemCount() * (this.itemHeight + this.itemSpacing) - this.itemSpacing;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        // Draw outlines and background
        if(this.listBackground != null)
        {
            int backgroundWidth = switch(this.scrollBarStyle) {
                case DETACHED -> OUTLINE_SIZE + this.contentPadding + this.getRowWidth() + this.contentPadding + OUTLINE_SIZE;
                case MERGED -> this.getWidth();
            };
            graphics.blitSprite(this.listBackground, this.getX(), this.getY(), backgroundWidth, this.getHeight());
        }

        // Draw items
        graphics.enableScissor(this.getRowLeft() - 1, this.getY() + 1, this.getRowRight() + 1, this.getY() + this.getHeight() - 1);
        this.renderListItems(graphics, mouseX, mouseY, partialTick);
        graphics.disableScissor();

        // Only draw scroll bar if enough items
        int maxScroll = this.getMaxScroll();
        if(maxScroll > 0 || this.scrollBarAlwaysVisible)
        {
            // Draw a background behind the scroll bar
            if(this.scrollBarStyle == ScrollBarStyle.DETACHED && this.listBackground != null)
            {
                graphics.blitSprite(this.listBackground, this.getScrollbarPosition() - this.contentPadding - OUTLINE_SIZE, this.getY(), OUTLINE_SIZE + this.contentPadding + this.scrollBarWidth + this.contentPadding + OUTLINE_SIZE, this.getHeight());
            }

            // Draw scroll bar
            int scrollBarStart = this.getScrollbarPosition();
            int scrollBarEnd = scrollBarStart + this.scrollBarWidth;
            int scrollBarHeight = this.getScrollbarHeight();
            int scrollBarTop = (int) (this.getScrollAreaTop() + (this.getScrollAreaHeight() - this.getScrollbarHeight()) * (this.getScrollAmount() / Math.max(maxScroll, 1)));
            boolean scrollBarHovered = ScreenUtil.isPointInArea(mouseX, mouseY, scrollBarStart, scrollBarTop, this.scrollBarWidth, scrollBarHeight);
            if(this.scrollBarSprites != null)
            {
                graphics.blitSprite(this.scrollBarSprites.get(false, (scrollBarHovered || this.scrolling) && maxScroll > 0), scrollBarStart, scrollBarTop, scrollBarEnd - scrollBarStart, scrollBarHeight);
            }
            else
            {
                // Fallback
                int scrollBarColour = scrollBarHovered ? 0xFF332E2D : 0xFF47403E;
                graphics.fill(scrollBarStart, scrollBarTop, scrollBarEnd, scrollBarTop + scrollBarHeight, scrollBarColour);
            }
        }
    }

    @Override
    protected void renderListItems(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        int rowLeft = this.getRowLeft();
        int rowWidth = this.getRowWidth();
        int rowHeight = this.itemHeight;
        int rowCount = this.getItemCount();

        // For efficiency, find the index to start drawing based on scroll amount
        int startIndex = Math.max(0, (int) ((this.getScrollAmount() - this.contentPadding) / (rowHeight + this.itemSpacing)));
        for(int i = startIndex; i < rowCount; i++)
        {
            int rowTop = this.getRowTop(i);
            if(rowTop <= this.getY() + this.getHeight())
            {
                this.renderItemBackground(graphics, i, rowLeft, rowTop, rowWidth, rowHeight, mouseX, mouseY);
                this.renderItem(graphics, mouseX, mouseY, partialTick, i, rowLeft, rowTop, rowWidth, rowHeight);
                continue;
            }
            // Break if the item is below the content area. Also stops drawing subsequent items.
            break;
        }
    }

    private void renderItemBackground(GuiGraphics graphics, int index, int rowLeft, int rowTop, int rowWidth, int rowHeight, int mouseX, int mouseY)
    {
        if(this.itemSprites != null)
        {
            boolean selected = this.isSelectedItem(index);
            boolean hovered = !this.scrolling && ScreenUtil.isPointInArea(mouseX, mouseY, rowLeft, rowTop, rowWidth, rowHeight);
            graphics.blitSprite(this.itemSprites.get(selected, hovered), rowLeft, rowTop, rowWidth, rowHeight);
        }
    }

    @Override
    protected void renderSelection(GuiGraphics graphics, int top, int rowWidth, int rowHeight, int outlineColour, int innerColour) {}

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        this.scrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
        {
            if(this.getFocused() != null && this.isDragging() && this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
            {
                return true;
            }
            if(this.scrolling)
            {
                double unitsPerScroll = (double) this.getMaxScroll() / Math.max(1, this.getScrollAreaHeight() - this.getScrollbarHeight());
                this.setScrollAmount(this.getScrollAmount() + deltaY * unitsPerScroll);
                return true;
            }
        }
        return false;
    }

    public E getEntry(double mouseX, double mouseY)
    {
        if(ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight()))
        {
            int rowLeft = this.getRowLeft();
            int rowWidth = this.getRowWidth();
            int rowHeight = this.itemHeight;
            int rowCount = this.getItemCount();
            int startIndex = Math.max(0, (int) ((this.getScrollAmount() - this.contentPadding) / (rowHeight + this.itemSpacing)));
            for(int i = startIndex; i < rowCount; i++)
            {
                int rowTop = this.getRowTop(i);
                if(rowTop <= this.getY() + this.getHeight())
                {
                    if(ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, rowLeft, rowTop, rowWidth, rowHeight))
                    {
                        return this.getEntry(i);
                    }
                    continue;
                }
                break;
            }
        }
        return null;
    }

    /* Hooked via Mixin to update the super scrolling state */
    public boolean updateScroll(double mouseX, double mouseY, int button)
    {
        this.scrolling = button == GLFW.GLFW_MOUSE_BUTTON_LEFT && ScreenUtil.isPointInArea((int) mouseX, (int) mouseY, this.getScrollbarPosition(), this.getScrollAreaTop(), this.scrollBarWidth, this.getScrollAreaHeight());
        return this.scrolling;
    }

    public enum ScrollBarStyle
    {
        DETACHED, MERGED
    }
}
