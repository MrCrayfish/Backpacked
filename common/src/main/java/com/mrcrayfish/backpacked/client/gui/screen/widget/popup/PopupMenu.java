package com.mrcrayfish.backpacked.client.gui.screen.widget.popup;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;

public interface PopupMenu extends Renderable, LayoutElement
{
    void hide();

    boolean mouseClicked(double mouseX, double mouseY, int button);
}
