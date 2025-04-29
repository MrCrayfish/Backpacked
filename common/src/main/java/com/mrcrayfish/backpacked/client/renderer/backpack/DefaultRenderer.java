package com.mrcrayfish.backpacked.client.renderer.backpack;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.Constants;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public class DefaultRenderer implements BackpackRenderer
{
    public static final Type TYPE = new Type(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "default"), MapCodec.unit(new DefaultRenderer()));

    @Override
    public void render(BackpackRenderContext context)
    {
        BakedModel model = context.itemRenderer().getItemModelShaper().getModelManager().getModel(context.backpack().getBaseModel());
        context.itemRenderer().render(context.stack(), ItemDisplayContext.NONE, false, context.pose(), context.source(), context.light(), OverlayTexture.NO_OVERLAY, model);
    }

    @Override
    public Type type()
    {
        return TYPE;
    }
}
