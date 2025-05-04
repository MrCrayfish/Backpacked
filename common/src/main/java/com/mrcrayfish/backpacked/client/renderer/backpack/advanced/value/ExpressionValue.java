package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.util.Utils;

import java.util.List;

public record ExpressionValue(List<Operation> operations) implements Value
{
    public static final Type TYPE = new Type(
        Utils.rl("expression"),
        RecordCodecBuilder.<ExpressionValue>mapCodec(builder -> builder.group(
            Operation.CODEC.listOf().fieldOf("operations").forGetter(o -> o.operations)
        ).apply(builder, ExpressionValue::new))
    );

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public double get(BackpackRenderContext context)
    {
        double result = 0;
        for(Operation op : this.operations)
        {
            result = op.operator().function().apply(result, op.source().get(context));
        }
        return 0;
    }

    public record Operation(Value source, Operator operator)
    {
        public static final Codec<Operation> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Value.EITHER_CODEC.fieldOf("value").forGetter(o -> o.source),
            Operator.CODEC.fieldOf("op").orElse(Operator.LAST).forGetter(o -> o.operator)
        ).apply(builder, Operation::new));
    }
}
