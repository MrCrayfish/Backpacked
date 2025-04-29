package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public interface BaseFunction
{
    Codec<BaseFunction> CODEC = Type.CODEC.dispatch(BaseFunction::type, Type::codec);

    Type type();

    void apply(BackpackRenderContext context);

    record Type(ResourceLocation id, MapCodec<? extends BaseFunction> codec)
    {
        public static final Codec<Type> CODEC = ResourceLocation.CODEC.flatXmap(id -> {
            Type serializer = FunctionTypes.getAll().get(id);
            if(serializer != null) {
                return DataResult.success(serializer);
            }
            return DataResult.error(() -> "Unregistered function: " + id);
        }, function -> {
            if(FunctionTypes.getAll().containsKey(function.id)) {
                return DataResult.success(function.id);
            }
            return DataResult.error(() -> "Unregistered function: " + function.id);
        });
    }
}
