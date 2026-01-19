package com.mrcrayfish.backpacked.integrations.jei;

import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackManagementScreen;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.Rect2i;

import java.util.Collections;
import java.util.List;

public class BackpackManagementScreenHandler implements IGuiContainerHandler<BackpackManagementScreen>
{
    @Override
    public List<Rect2i> getGuiExtraAreas(BackpackManagementScreen screen)
    {
        ScreenRectangle area = screen.getBackButtonArea();
        if(area != null)
        {
            return List.of(new Rect2i(area.left(), area.top(), area.width(), area.height()));
        }
        return Collections.emptyList();
    }
}
