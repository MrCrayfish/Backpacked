package com.mrcrayfish.backpacked.client.renderer.backpack;

import com.mojang.serialization.Codec;
import com.mrcrayfish.backpacked.platform.ClientServices;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.resources.model.BakedModel;

public class DefaultRenderer implements BackpackRenderer
{
    public static final Type TYPE = new Type(Utils.rl("default"), Codec.unit(new DefaultRenderer()));

    @Override
    public void render(BackpackRenderContext context)
    {
        if(context.renderMode().canDrawModels())
        {
            BakedModel model = ClientServices.CLIENT.getBakedModel(context.backpack().getBaseModel());
            context.bakedModelRenderer().accept(model);
        }
    }

    @Override
    public Type type()
    {
        return TYPE;
    }
}
