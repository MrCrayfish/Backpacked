package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

import java.util.function.BiFunction;

public enum Operator implements StringRepresentable
{
    FIRST("first", (a, b) -> a),
    LAST("last", (a, b) -> b),
    ADD("add", Double::sum),
    SUBTRACT("subtract", (a, b) -> a - b),
    MULTIPLY("multiply", (a, b) -> a * b),
    DIVIDE("divide", (a, b) -> a / b),
    MODULO("modulo", (a, b) -> a % b),
    POSITIVE_MODULO("positive_modulo", Mth::positiveModulo),
    MIN("min", Math::min),
    MAX("max", Math::max),
    POWER("power", Math::pow);

    public static final Codec<Operator> CODEC = StringRepresentable.fromEnum(Operator::values);

    private final String name;
    private final BiFunction<Double, Double, Double> function;

    Operator(String name, BiFunction<Double, Double, Double> function)
    {
        this.name = name;
        this.function = function;
    }

    public BiFunction<Double, Double, Double> function()
    {
        return this.function;
    }

    @Override
    public String getSerializedName()
    {
        return this.name;
    }
}
