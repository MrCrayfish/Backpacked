package com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.item;

import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.dropdown.MenuItem;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.function.Function;

public class CheckboxItem extends MenuItem
{
    private static final WidgetSprites SPRITES = new WidgetSprites(
            Utils.rl("backpack/toggle_on"),
            Utils.rl("backpack/toggle_off"),
            Utils.rl("backpack/toggle_on")
    );
    private static final int CHECK_BOX_SIZE = 6;

    private final MutableBoolean holder;
    private final Function<Boolean, Boolean> callback;

    private CheckboxItem(Component label, MutableBoolean holder, Function<Boolean, Boolean> callback)
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
            this.getPopupMenu().deepClose();
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

    public static MenuItem create(Component label, MutableBoolean value, Function<Boolean, Boolean> callbackHandler)
    {
        return new CheckboxItem(label, value, callbackHandler);
    }
}
