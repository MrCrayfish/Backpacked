package com.mrcrayfish.backpacked.client.renderer.backpack;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.StandaloneModels;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import net.minecraft.resources.Identifier;

public class DefaultRenderer implements BackpackRenderer
{
    public static final Type TYPE = new Type(Utils.id("default"), MapCodec.unit(new DefaultRenderer()));

    @Override
    public void render(BackpackRenderContext context)
    {
        if(context.renderMode().canDrawModels())
        {
            FrameworkModelResource<FrameworkBakedModel> model = StandaloneModels.getResource(context.baseModel());
            if(model != null)
            {
                context.modelRenderer().accept(model.getModel());
            }
        }
    }

    @Override
    public Type type()
    {
        return TYPE;
    }
}
