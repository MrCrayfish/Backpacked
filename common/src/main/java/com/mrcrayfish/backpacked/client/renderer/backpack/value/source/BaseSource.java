package com.mrcrayfish.backpacked.client.renderer.backpack.value.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public interface BaseSource
{
    Codec<BaseSource> CODEC = Type.CODEC.dispatch(Type::get, Type::codec);

    Type type();

    double apply(BackpackRenderContext context);

    record Type(ResourceLocation id, Codec<? extends BaseSource> codec)
    {
        private static final Codec<Type> CODEC = ResourceLocation.CODEC.flatXmap(id -> {
            Type codec = SourceTypes.getAll().get(id);
            if(codec != null) {
                return DataResult.success(codec);
            }
            return DataResult.error(() -> "Unregistered dynamic value source: " + id);
        }, codec -> {
            if(SourceTypes.getAll().containsKey(codec.id)) {
                return DataResult.success(codec.id);
            }
            return DataResult.error(() -> "Unregistered dynamic value source: " + codec.id);
        });

        private static Type get(BaseSource source)
        {
            ResourceLocation id = source.type().id();
            if(!SourceTypes.getAll().containsKey(id))
                throw new IllegalArgumentException("Unregistered dynamic value source: " + id);
            return SourceTypes.getAll().get(id);
        }
    }
}
