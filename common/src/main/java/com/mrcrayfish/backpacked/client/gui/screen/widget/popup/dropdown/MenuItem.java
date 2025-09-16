package com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class MenuItem extends AbstractWidget
{
    protected static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/dropdown/menu_item"),
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/dropdown/menu_item_hovered")
    );

    DropdownMenu parent;

    public MenuItem(Component label)
    {
        super(0, 0, 100, 20, label);
    }

    protected DropdownMenu getParent()
    {
        return this.parent;
    }

    protected boolean selected()
    {
        return false;
    }

    protected void visitChildMenus(Consumer<PopupMenu> consumer) {}

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTick)
    {
        RenderSystem.enableBlend();
        boolean hovered = this.getParent() != null && !this.getParent().hasChild() && this.isHovered();
        graphics.blitSprite(SPRITES.get(this.active, hovered || this.selected()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        RenderSystem.disableBlend();

        Font font = Minecraft.getInstance().font;
        int offset = (this.getHeight() - font.lineHeight) / 2 + 1;
        graphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX() + offset, this.getY() + offset, 0xFFFFFFFF);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output)
    {
        output.add(NarratedElementType.TITLE, this.getMessage());
    }

    protected int calculateWidth()
    {
        Font font = Minecraft.getInstance().font;
        int labelOffset = (this.getHeight() - font.lineHeight) / 2 + 1;
        int labelWidth = font.width(this.getMessage());
        return labelOffset + labelWidth + labelOffset;
    }

    public static MenuItem button(Component label, Runnable clickHandler)
    {
        return new Button(label, clickHandler);
    }

    public static MenuItem checkbox(Component label, MutableBoolean value, Function<Boolean, Boolean> callbackHandler)
    {
        return new Checkbox(label, value, callbackHandler);
    }

    public static MenuItem popup(Component label, PopupMenu menu)
    {
        return new Popup(label, menu);
    }

    public static MenuItem dropdown(Component label, DropdownMenu menu)
    {
        return new Dropdown(label, menu);
    }

    static class Button extends MenuItem
    {
        private final Runnable action;

        public Button(Component label, Runnable action)
        {
            super(label);
            this.action = action;
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            this.action.run();
            this.getParent().deepClose();
        }
    }

    static class Checkbox extends MenuItem
    {
        private static final WidgetSprites SPRITES = new WidgetSprites(
            Utils.rl("backpack/toggle_on"),
            Utils.rl("backpack/toggle_off"),
            Utils.rl("backpack/toggle_on")
        );
        private static final int CHECK_BOX_SIZE = 6;

        private final MutableBoolean holder;
        private final Function<Boolean, Boolean> callback;

        public Checkbox(Component label, MutableBoolean holder, Function<Boolean, Boolean> callback)
        {
            super(label);
            this.holder = holder;
            this.callback = callback;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTick)
        {
            super.renderWidget(graphics, mouseX, mouseY, deltaTick);
            int yOffset = (this.getHeight() - CHECK_BOX_SIZE) / 2;
            int stateIconY = this.getY() + yOffset;
            int stateIconX = this.getX() + this.getWidth() - CHECK_BOX_SIZE - yOffset;
            graphics.blitSprite(SPRITES.get(this.holder.booleanValue(), this.isHovered()), stateIconX, stateIconY, CHECK_BOX_SIZE, CHECK_BOX_SIZE);
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            boolean newValue = !this.holder.getValue();
            this.holder.setValue(newValue);
            if(this.callback.apply(newValue))
            {
                this.getParent().deepClose();
            }
        }

        @Override
        protected int calculateWidth()
        {
            Font font = Minecraft.getInstance().font;
            int labelOffset = (this.getHeight() - font.lineHeight) / 2 + 1;
            int labelWidth = font.width(this.getMessage());
            int checkboxOffset = (this.getHeight() - CHECK_BOX_SIZE) / 2;
            return labelOffset + labelWidth + labelOffset + CHECK_BOX_SIZE + checkboxOffset;
        }
    }

    static class Popup extends MenuItem
    {
        final PopupMenu child;

        public Popup(Component label, PopupMenu child)
        {
            super(label);
            this.child = child;
        }

        @Override
        protected void visitChildMenus(Consumer<PopupMenu> consumer)
        {
            consumer.accept(this.child);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTick)
        {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            if(this.selected())
            {
                poseStack.translate(0, 0, 51);
            }
            super.renderWidget(graphics, mouseX, mouseY, deltaTick);
            Font font = Minecraft.getInstance().font;
            int top = this.getY() + (this.getHeight() - font.lineHeight) / 2 + 1;
            graphics.drawString(Minecraft.getInstance().font, ">", this.getX() + this.getWidth() - 10, top, 0xFFFFFFFF);
            poseStack.popPose();
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            this.getParent().showChild(this.child, this.getRectangle());
        }

        @Override
        protected boolean selected()
        {
            return this.getParent().isChild(this.child);
        }

        @Override
        protected int calculateWidth()
        {
            Font font = Minecraft.getInstance().font;
            int labelOffset = (this.getHeight() - font.lineHeight) / 2 + 1;
            int labelWidth = font.width(this.getMessage());
            int arrowWidth = font.width(">");
            return labelOffset + labelWidth + labelOffset + arrowWidth + labelOffset;
        }
    }

    static class Dropdown extends MenuItem
    {
        final DropdownMenu subMenu;

        public Dropdown(Component label, DropdownMenu subMenu)
        {
            super(label);
            this.subMenu = subMenu;
        }

        @Override
        protected void visitChildMenus(Consumer<PopupMenu> consumer)
        {
            consumer.accept(this.subMenu);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float deltaTick)
        {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            if(this.selected())
            {
                poseStack.translate(0, 0, 51);
            }
            super.renderWidget(graphics, mouseX, mouseY, deltaTick);
            Font font = Minecraft.getInstance().font;
            int top = this.getY() + (this.getHeight() - font.lineHeight) / 2 + 1;
            graphics.drawString(Minecraft.getInstance().font, ">", this.getX() + this.getWidth() - 10, top, 0xFFFFFFFF);
            poseStack.popPose();
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            this.getParent().showChild(this.subMenu, this.getRectangle());
        }

        @Override
        protected boolean selected()
        {
            return this.getParent().isChild(this.subMenu);
        }

        @Override
        protected int calculateWidth()
        {
            Font font = Minecraft.getInstance().font;
            int labelOffset = (this.getHeight() - font.lineHeight) / 2 + 1;
            int labelWidth = font.width(this.getMessage());
            int arrowWidth = font.width(">");
            return labelOffset + labelWidth + labelOffset + arrowWidth + labelOffset;
        }
    }
}
