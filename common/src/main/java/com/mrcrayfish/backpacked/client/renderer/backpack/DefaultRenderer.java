package com.mrcrayfish.backpacked.client.renderer.backpack;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;

public class DefaultRenderer implements BackpackRenderer
{
    public static final Type TYPE = new Type(Utils.rl("default"), MapCodec.unit(new DefaultRenderer()));

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
