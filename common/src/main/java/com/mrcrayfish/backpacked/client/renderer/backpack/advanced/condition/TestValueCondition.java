package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value.Value;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.util.StringRepresentable;

import java.util.Objects;
import java.util.function.BiFunction;

public record TestValueCondition(Value first, Value second, Comparison comparison) implements BaseCondition
{
    public static final Type TYPE = new Type(Utils.rl("test_value"), RecordCodecBuilder.<TestValueCondition>mapCodec(builder -> builder.group(
        Value.EITHER_CODEC.fieldOf("first").forGetter(o -> o.first),
        Value.EITHER_CODEC.fieldOf("second").forGetter(o -> o.second),
        Comparison.CODEC.fieldOf("comparison").forGetter(o -> o.comparison)
    ).apply(builder, TestValueCondition::new)));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public boolean test(BackpackRenderContext context)
    {
        double first = this.first.get(context);
        double second = this.second.get(context);
        return this.comparison.function.apply(first, second);
    }

    private enum Comparison implements StringRepresentable
    {
        EQUAL("equal", Objects::equals),
        NOT_EQUAL("not_equal", (a, b) -> !Objects.equals(a, b)),
        GREATER_THAN("greater_than", (a, b) -> a > b),
        LESS_THAN("less_than", (a, b) -> a < b),
        GREATER_THAN_OR_EQUAL("greater_than_or_equal", (a, b) -> a >= b),
        LESS_THAN_OR_EQUAL("less_than_or_equal", (a, b) -> a <= b);

        public static final Codec<Comparison> CODEC = StringRepresentable.fromEnum(Comparison::values);

        private final String name;
        private final BiFunction<Double, Double, Boolean> function;

        Comparison(String name, BiFunction<Double, Double, Boolean> function)
        {
            this.name = name;
            this.function = function;
        }

        @Override
        public String getSerializedName()
        {
            return this.name;
        }
    }
}
