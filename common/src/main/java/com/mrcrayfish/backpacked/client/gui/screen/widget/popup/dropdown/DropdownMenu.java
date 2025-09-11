package com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.client.gui.screen.layout.BorderedLinearLayout;
import com.mrcrayfish.backpacked.util.ScreenUtil;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class DropdownMenu extends AbstractWidget implements PopupMenu
{
    private final PopupMenuHandler handler;
    private final BorderedLinearLayout layout = (BorderedLinearLayout)
        BorderedLinearLayout.vertical().border(3).spacing(2);
    private final List<AbstractWidget> items = new ArrayList<>();
    private Alignment alignment = Alignment.BELOW_LEFT;
    private @Nullable ResourceLocation background;
    @Nullable DropdownMenu parent;
    @Nullable DropdownMenu subMenu;

    private DropdownMenu(PopupMenuHandler handler)
    {
        super(0, 0, 0, 0, CommonComponents.EMPTY);
        this.handler = handler;
        this.visible = false;
    }

    private void setAlignment(Alignment alignment)
    {
        this.alignment = alignment;
    }

    public void toggle(int mouseX, int mouseY)
    {
        this.toggle(new ScreenRectangle(mouseX, mouseY, 0, 0));
    }

    public void toggle(AbstractWidget widget)
    {
        this.toggle(widget.getRectangle());
    }

    public void toggle(ScreenRectangle rect)
    {
        if(!this.visible)
        {
            this.show(rect);
        }
        else
        {
            this.hide();
        }
    }

    void show(ScreenRectangle rect)
    {
        this.updatePosition(rect);
        this.items.forEach(child -> {
            child.visible = true;
        });
        this.visible = true;
        if(this.parent == null)
        {
            this.handler.setPopupMenu(this);
        }
    }

    public void hide()
    {
        this.items.forEach(child -> {
            child.visible = false;
            if(child instanceof MenuItem.Dropdown menu) {
                menu.subMenu.hide();
            }
        });
        this.subMenu = null;
        this.visible = false;
    }

    private void updatePosition(ScreenRectangle rect)
    {
        this.layout.arrangeElements();
        this.width = this.layout.getWidth();
        this.height = this.layout.getHeight();
        this.alignment.aligner().accept(this, rect);
        this.layout.setX(this.getX());
        this.layout.setY(this.getY());
    }

    public void addItem(MenuItem item)
    {
        item.setParent(this);
        this.layout.addChild(item);
        this.items.add(item);
        item.visible = false;
    }

    void deepClose()
    {
        this.handler.setPopupMenu(null);
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

        if(this.background != null)
        {
            graphics.blitSprite(this.background, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }

        this.items.forEach(widget -> widget.render(graphics, mouseX, mouseY, deltaTick));

        if(this.subMenu != null)
        {
            this.subMenu.render(graphics, mouseX, mouseY, deltaTick);
        }

        poseStack.popPose();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(!this.active || !this.visible)
            return false;

        if(this.subMenu != null)
        {
            if(this.subMenu.mouseClicked(mouseX, mouseY, button))
            {
                return true;
            }
            if(this.getRectangle().containsPoint((int) mouseX, (int) mouseY))
            {
                this.subMenu.hide();
                this.subMenu = null;
                return true;
            }
        }

        for(AbstractWidget widget : this.items)
        {
            if(widget.mouseClicked(mouseX, mouseY, button))
            {
                return true;
            }
        }

        return this.subMenu == null && this.getRectangle().containsPoint((int) mouseX, (int) mouseY);
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer)
    {
        this.layout.visitWidgets(consumer);
    }

    public static Builder builder(PopupMenuHandler handler)
    {
        return new Builder(handler);
    }

    public static class Builder
    {
        private final PopupMenuHandler handler;
        private final DropdownMenu base;
        private final List<MenuItem> items = new ArrayList<>();
        private int minItemWidth = 0;
        private int minItemHeight = 20;
        private @Nullable ResourceLocation background = Utils.rl("backpack/dropdown/background");
        private @Nullable Integer padding;
        private @Nullable Integer spacing;

        private Builder(PopupMenuHandler handler)
        {
            this.handler = handler;
            this.base = new DropdownMenu(handler);
        }

        public Builder setMinItemSize(int width, int height)
        {
            this.minItemWidth = width;
            this.minItemHeight = height;
            return this;
        }

        public Builder setBackground(@Nullable ResourceLocation background)
        {
            this.background = background;
            return this;
        }

        public Builder setPadding(int padding)
        {
            this.padding = padding;
            return this;
        }

        public Builder setSpacing(int spacing)
        {
            this.spacing = spacing;
            return this;
        }

        public Builder setAlignment(Alignment alignment)
        {
            this.base.setAlignment(alignment);
            return this;
        }

        public Builder addItem(MenuItem item)
        {
            this.items.add(item);
            return this;
        }

        public DropdownMenu build()
        {
            this.base.items.clear();
            int maxWidth = this.items.stream().mapToInt(MenuItem::calculateWidth).max().orElse(100);
            this.items.forEach(item -> {
                item.setSize(Math.max(maxWidth, this.minItemWidth), this.minItemHeight);
                this.base.addItem(item);
            });
            this.base.background = this.background;
            if(this.padding != null)
            {
                this.base.layout.border(this.padding);
            }
            return this.base;
        }
    }
}
