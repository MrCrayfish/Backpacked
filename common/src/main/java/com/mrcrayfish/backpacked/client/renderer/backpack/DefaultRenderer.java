package com.mrcrayfish.backpacked.client.renderer.backpack;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

public class DefaultRenderer implements BackpackRenderer
{
    public static final Type TYPE = new Type(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "default"), MapCodec.unit(new DefaultRenderer()));

    @Override
    public void render(BackpackRenderContext context)
    {
        if(context.renderMode().canDrawModels())
        {
            BakedModel model = Minecraft.getInstance().getModelManager().getModel(context.backpack().getBaseModel());
            context.bakedModelRenderer().accept(model);
        }
    }

    @Override
    public Type type()
    {
        return TYPE;
    }
}
