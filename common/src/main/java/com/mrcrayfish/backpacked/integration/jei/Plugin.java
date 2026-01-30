package com.mrcrayfish.backpacked.integration.jei;

import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackManagementScreen;
import com.mrcrayfish.backpacked.client.gui.screen.inventory.BackpackScreen;
import com.mrcrayfish.backpacked.util.Utils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class Plugin implements IModPlugin // TODO DONE
{
    @Override
    public ResourceLocation getPluginUid()
    {
        return Utils.rl("plugin");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        registration.addGenericGuiContainerHandler(BackpackScreen.class, new BackpackScreenHandler());
        registration.addGenericGuiContainerHandler(BackpackManagementScreen.class, new BackpackManagementScreenHandler());
    }
}
