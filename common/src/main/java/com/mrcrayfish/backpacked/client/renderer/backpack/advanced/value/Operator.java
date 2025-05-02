package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.BiFunction;

public enum Operator implements StringRepresentable
{
    FIRST((a, b) -> a),
    LAST((a, b) -> b),
    ADD(Double::sum),
    SUBTRACT((a, b) -> a - b),
    MULTIPLY((a, b) -> a * b),
    DIVIDE((a, b) -> a / b),
    MIN(Math::min),
    MAX(Math::max);

    public static final Codec<Operator> CODEC = StringRepresentable.fromEnum(Operator::values);

    private final BiFunction<Double, Double, Double> function;

    Operator(BiFunction<Double, Double, Double> function)
    {
        this.function = function;
    }

    public BiFunction<Double, Double, Double> function()
    {
        return this.function;
    }

    @Override
    public String getSerializedName()
    {
        return this.name().toLowerCase(Locale.ENGLISH);
    }
}
