package com.mrcrayfish.backpacked.client.renderer.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public interface BackpackRenderer
{
    Codec<BackpackRenderer> CODEC = BackpackRenderer.Type.CODEC.dispatch(Type::get, Type::codec);

    void render(BackpackRenderContext context);

    Type type();

    record Type(ResourceLocation id, MapCodec<? extends BackpackRenderer> codec)
    {
        private static final Codec<BackpackRenderer.Type> CODEC = ResourceLocation.CODEC.flatXmap(id -> {
            BackpackRenderer.Type codec = RendererTypes.getAll().get(id);
            if(codec != null) {
                return DataResult.success(codec);
            }
            return DataResult.error(() -> "Unregistered backpack renderer: " + id);
        }, codec -> {
            if(RendererTypes.getAll().containsKey(codec.id)) {
                return DataResult.success(codec.id);
            }
            return DataResult.error(() -> "Unregistered backpack renderer: " + codec.id);
        });

        private static BackpackRenderer.Type get(BackpackRenderer renderer)
        {
            ResourceLocation id = renderer.type().id();
            if(!RendererTypes.getAll().containsKey(id))
                throw new IllegalArgumentException("Unregistered backpack renderer: " + id);
            return RendererTypes.getAll().get(id);
        }
    }
}
