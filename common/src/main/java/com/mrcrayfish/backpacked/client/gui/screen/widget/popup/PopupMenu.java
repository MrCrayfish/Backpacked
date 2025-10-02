package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.client.gui.screen.layout.PaddedLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class PopupMenu extends AbstractWidget implements ContainerEventHandler
{
    private final PopupMenuHandler handler;
    private @Nullable List<AbstractWidget> cachedWidgets;
    private Alignment alignment = Alignment.END_TOP;
    private @Nullable ResourceLocation background;
    protected @Nullable PopupMenu parent;
    protected @Nullable PopupMenu child;
    protected @Nullable GuiEventListener focused;
    protected boolean dragging;

    public PopupMenu(PopupMenuHandler handler)
    {
        super(0, 0, 0, 0, CommonComponents.EMPTY);
        this.handler = handler;
        this.visible = false;
    }

    protected abstract Layout layout();

    protected int padding()
    {
        return this.layout() instanceof PaddedLayout layout ? layout.padding() : 0;
    }

    protected void setAlignment(Alignment alignment)
    {
        this.alignment = alignment;
    }

    protected void setBackground(@Nullable ResourceLocation background)
    {
        this.background = background;
    }

    private List<AbstractWidget> getWidgets()
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
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTick)
    {
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(0, 0, 10);

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
            this.child.render(graphics, mouseX, mouseY, deltaTick);
        }

        poseStack.popPose();
    }

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        // If the menu is not active or visible, ignore the click event
        if(!this.active || !this.visible)
            return false;

        // If a child menu was spawned off this menu, send the event to the child first
        if(this.child != null)
        {
            // If the child handled the event, prevent further handling. Only one result should
            // occur when handling the click event.
            if(this.child.mouseClicked(mouseX, mouseY, button))
            {
                return true;
            }

            // Otherwise if mouse was clicked within the bounds of this menu, we want to bring back
            // focus to this menu by hiding the child menu. This means this menu will be at the top
            // of the stack, and the child will be popped off.
            if(this.getRectangle().containsPoint((int) mouseX, (int) mouseY))
            {
                this.child.hide();
                this.child = null;
                return true;
            }
        }

        // Send the event to widgets, and return true on first handled
        for(AbstractWidget widget : this.getWidgets())
        {
            if(widget.mouseClicked(mouseX, mouseY, button))
            {
                this.setFocused(widget);
                if(button == 0)
                {
                    this.setDragging(true);
                }
                return true;
            }
        }

        // If click occurred inside the menu, just mark as handled to prevent it from hiding
        if(this.child == null && this.getRectangle().containsPoint((int) mouseX, (int) mouseY))
        {
            this.setFocused(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        return this.getFocused() != null && this.isDragging() && button == 0 && this.getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(button == 0 && this.isDragging())
        {
            this.setDragging(false);
            if(this.getFocused() != null)
            {
                return this.getFocused().mouseReleased(mouseX, mouseY, button);
            }
        }
        return this.getChildAt(mouseX, mouseY).filter(listener -> listener.mouseReleased(mouseX, mouseY, button)).isPresent();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    @Override
    public List<? extends GuiEventListener> children()
    {
        return this.getWidgets();
    }

    @Override
    public @Nullable GuiEventListener getFocused()
    {
        if(this.parent != null)
        {
            return this.parent.getFocused();
        }
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener listener)
    {
        // The root popup should be the controller
        if(this.parent != null)
        {
            this.parent.setFocused(listener);
            return;
        }
        if(this.focused != null)
        {
            this.focused.setFocused(false);
        }
        if(listener != null)
        {
            listener.setFocused(true);
        }
        this.focused = listener;
    }

    @Override
    public void setDragging(boolean dragging)
    {
        if(this.parent != null)
        {
            this.parent.setDragging(dragging);
            return;
        }
        this.dragging = dragging;
    }

    @Override
    public boolean isDragging()
    {
        if(this.parent != null)
        {
            return this.parent.isDragging();
        }
        return this.dragging;
    }

    public void show(AbstractWidget widget)
    {
        this.show(widget.getRectangle());
    }

    public void show(ScreenRectangle rect)
    {
        this.updatePosition(rect);
        this.visible = true;
        if(this.parent == null)
        {
            this.handler.setPopupMenu(this);
        }
        else if(this.parent.visible)
        {
            if(this.parent.child != null)
            {
                this.parent.child.hide();
            }
            this.parent.child = this;
        }
        else
        {
            this.visible = false;
        }
    }

    public void hide()
    {
        if(this.child != null)
        {
            this.child.hide();
        }
        this.child = null;
        this.visible = false;
        this.setFocused(null);
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
    }

    public void deepClose()
    {
        this.handler.setPopupMenu(null);
    }

    protected void adoptChild(PopupMenu menu)
    {
        menu.parent = this;
    }

    public boolean isActiveChildMenu(PopupMenu child)
    {
        return this.child == child;
    }
}
