package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.client.renderer.BackpackItemSpecialRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class BackpackClientItemExtensions implements IClientItemExtensions
{
    private final BackpackItemSpecialRenderer renderer;

    public BackpackClientItemExtensions()
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
