package com.mrcrayfish.backpacked.integrations.jei;

import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;

import java.util.List;

public class BackpackScreenHandler implements IGuiContainerHandler<BackpackScreen>
{
    @Override
    public List<Rect2i> getGuiExtraAreas(BackpackScreen screen)
    {
        return screen.getLayouts().stream().map(area -> {
            int x = area.getX() - BackpackScreen.LABEL_PADDING;
            int y = area.getY() - BackpackScreen.LABEL_PADDING;
            int width = area.getWidth() + BackpackScreen.LABEL_PADDING * 2;
            int height = area.getHeight() + BackpackScreen.LABEL_PADDING * 2;
            return new Rect2i(x, y, width, height);
        }).toList();
    }
}
