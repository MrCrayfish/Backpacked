package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public interface Value
{
    Codec<Value> CODEC = Type.CODEC.dispatch(Type::get, Type::codec);
    Codec<Value> EITHER_CODEC = Codec.either(Codec.DOUBLE, Value.CODEC).xmap(either -> {
        return either.map(ConstantValue::new, Function.identity());
    }, value -> {
        if(value instanceof ConstantValue(double v)) {
            return Either.left(v);
        }
        return Either.right(value);
    });
    Value ZERO = new ConstantValue(0);

    Type type();

    double get(BackpackRenderContext context);

    record Type(ResourceLocation id, MapCodec<? extends Value> codec)
    {
        private static final Codec<Type> CODEC = ResourceLocation.CODEC.flatXmap(id -> {
            Type codec = ValueTypes.getAll().get(id);
            if(codec != null) {
                return DataResult.success(codec);
            }
            return DataResult.error(() -> "Unregistered value type: " + id);
        }, codec -> {
            if(ValueTypes.getAll().containsKey(codec.id)) {
                return DataResult.success(codec.id);
            }
            return DataResult.error(() -> "Unregistered value type: " + codec.id);
        });

        private static Type get(Value source)
        {
            ResourceLocation id = source.type().id();
            if(!ValueTypes.getAll().containsKey(id))
                throw new IllegalArgumentException("Unregistered value type: " + id);
            return ValueTypes.getAll().get(id);
        }
    }
}
