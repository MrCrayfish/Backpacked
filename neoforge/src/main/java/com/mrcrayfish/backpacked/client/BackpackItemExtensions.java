package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.client.renderer.BackpackItemSpecialRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class BackpackItemExtensions implements IClientItemExtensions
{
    private final BackpackItemSpecialRenderer renderer;

    public BackpackItemExtensions()
    {
        Minecraft mc = Minecraft.getInstance();
        this.renderer = new BackpackItemSpecialRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
    }

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer()
    {
        return this.renderer;
    }
}
