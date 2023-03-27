package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.platform.services.IScreenHelper;
import com.mrcrayfish.configured.client.screen.ModConfigSelectionScreen;
import com.mrcrayfish.configured.integration.CatalogueConfigFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;

/**
 * Author: MrCrayfish
 */
public class FabricScreenHelper implements IScreenHelper
{
    @Override
    public void openConfigScreen()
    {
        FabricLoader.getInstance().getModContainer(Constants.MOD_ID).ifPresent(container -> {
            Screen screen = CatalogueConfigFactory.createConfigScreen(Minecraft.getInstance().screen, container);
            Minecraft.getInstance().setScreen(screen);
        });
    }

    @Override
    public int getScreenLeftPos(AbstractContainerScreen<?> screen)
    {
        return screen.leftPos;
    }

    @Override
    public int getScreenTopPos(AbstractContainerScreen<?> screen)
    {
        return screen.topPos;
    }

    @Override
    public Slot createCreativeSlotWrapper(Slot slot, int index, int x, int y)
    {
        return new CreativeModeInventoryScreen.SlotWrapper(slot, index, x, y);
    }
}
