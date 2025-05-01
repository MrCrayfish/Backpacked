package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value.source.BaseSource;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value.source.ConstantSource;

import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public final class Value
{
    public static final Value ZERO = new Value(new ConstantSource(0), 0.0, 1.0);
    public static final Codec<Value> VALUE_CODEC = RecordCodecBuilder.create(builder -> builder.group(
        BaseSource.CODEC.fieldOf("source").forGetter(o -> o.source),
        Codec.DOUBLE.optionalFieldOf("base", 0.0).forGetter(o -> o.base),
        Codec.DOUBLE.optionalFieldOf("scale", 1.0).forGetter(o -> o.scale)
    ).apply(builder, Value::new));

    // We want to accept either a raw double or a full value object
    public static final Codec<Value> CODEC = Codec.either(Codec.DOUBLE, VALUE_CODEC).xmap(either -> {
        return either.map(val -> new Value(new ConstantSource(val), 0.0, 1.0), Function.identity());
    }, value -> {
        if(value.source instanceof ConstantSource source) {
            return Either.left(source.value());
        }
        return Either.right(value);
    });

    private final BaseSource source;
    private final double scale;
    private final double base;

    public Value(BaseSource source, double base, double scale)
    {
        this.source = source;
        this.base = base;
        this.scale = scale;
    }

    public double getValue(BackpackRenderContext context)
    {
        return (this.base + this.source.apply(context)) * this.scale;
    }
}
