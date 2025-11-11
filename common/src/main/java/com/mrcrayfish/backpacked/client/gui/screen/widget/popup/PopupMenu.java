package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.client.gui.screen.layout.PaddedLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class PopupMenu implements Renderable, GuiEventListener, LayoutElement
{
    final PopupMenuController controller;
    @Nullable PopupMenu parent;
    @Nullable PopupMenu child;

    private int x;
    private int y;
    private int width;
    private int height;
    private @Nullable List<AbstractWidget> cachedWidgets;
    private Alignment alignment = Alignment.END_TOP;
    private @Nullable ResourceLocation background;
    private int screenClampPadding = 0;

    protected PopupMenu(PopupMenuHandler handler)
    {
        this.controller = handler.getPopupMenuController();
    }

    protected abstract Layout layout();

    protected int padding()
    {
        return this.layout() instanceof PaddedLayout layout ? layout.padding() : 0;
    }

    @Override
    public void setX(int x)
    {
        this.x = x;
    }

    @Override
    public void setY(int y)
    {
        this.y = y;
    }

    @Override
    public int getX()
    {
        return this.x;
    }

    @Override
    public int getY()
    {
        return this.y;
    }

    @Override
    public int getWidth()
    {
        return this.width;
    }

    @Override
    public int getHeight()
    {
        return this.height;
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer)
    {
        this.layout().visitWidgets(consumer);
    }

    protected void setAlignment(Alignment alignment)
    {
        this.alignment = alignment;
    }

    protected void setBackground(@Nullable ResourceLocation background)
    {
        this.background = background;
    }

    List<AbstractWidget> getWidgets()
    {
        if(this.cachedWidgets == null)
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            this.layout().visitWidgets(widgets::add);
            this.cachedWidgets = List.copyOf(widgets);
        }
        return this.cachedWidgets;
    }

    protected void invalidateWidgets()
    {
        this.cachedWidgets = null;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTick)
    {
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        graphics.fill(0, 0, window.getWidth(), window.getHeight(), 0x50000000);

        // Draw the background of the popup if present
        if(this.background != null)
        {
            graphics.blitSprite(this.background, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

        // Draw all widgets from the layout
        this.getWidgets().forEach(widget -> widget.render(graphics, mouseX, mouseY, deltaTick));

        if(this.child != null)
        {
            graphics.pose().translate(0, 0, 350);
            this.child.render(graphics, mouseX, mouseY, deltaTick);
        }
    }

    @Override
    public void setFocused(boolean focused) {}

    @Override
    public boolean isFocused()
    {
        return this.controller.top() == this;
    }

    @Override
    public ScreenRectangle getRectangle()
    {
        return new ScreenRectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public void show(AbstractWidget widget)
    {
        this.show(widget.getRectangle());
    }

    public void show(ScreenRectangle rect)
    {
        this.updatePosition(rect);
        this.controller.open(this);
    }

    public void close()
    {
        this.controller.close(this);
    }

    public void deepClose()
    {
        this.controller.closeAll();
    }

    private void updatePosition(ScreenRectangle rect)
    {
        Layout layout = this.layout();
        layout.arrangeElements();
        this.width = layout.getWidth();
        this.height = layout.getHeight();
        this.alignment.aligner().accept(this, rect);
        layout.setX(this.getX());
        layout.setY(this.getY());
        this.clampToWindow();
    }

    private void clampToWindow()
    {
        Window window = Minecraft.getInstance().getWindow();
        this.setX(Mth.clamp(this.getX(), this.screenClampPadding, window.getGuiScaledWidth() - this.getWidth() - this.screenClampPadding));
        this.setY(Mth.clamp(this.getY(), this.screenClampPadding, window.getGuiScaledHeight() - this.getHeight() - this.screenClampPadding));
        Layout layout = this.layout();
        layout.setX(this.getX());
        layout.setY(this.getY());
    }

    public boolean hasChild()
    {
        return this.child != null;
    }

    public void setScreenClampPadding(int screenClampPadding)
    {
        this.screenClampPadding = screenClampPadding;
    }
}
