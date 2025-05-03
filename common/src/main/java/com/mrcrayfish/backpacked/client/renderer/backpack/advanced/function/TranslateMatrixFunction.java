package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value.Value;
import com.mrcrayfish.backpacked.util.Utils;

/**
 * Author: MrCrayfish
 */
public record TranslateMatrixFunction(Value x, Value y, Value z) implements BaseFunction
{
    public static final Type TYPE = new Type(
        Utils.rl("translate_matrix"),
        RecordCodecBuilder.<TranslateMatrixFunction>mapCodec(builder -> builder.group(
            Value.EITHER_CODEC.fieldOf("x").orElse(Value.ZERO).forGetter(o -> o.x),
            Value.EITHER_CODEC.fieldOf("y").orElse(Value.ZERO).forGetter(o -> o.y),
            Value.EITHER_CODEC.fieldOf("z").orElse(Value.ZERO).forGetter(o -> o.z)
        ).apply(builder, TranslateMatrixFunction::new))
    );

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public void apply(BackpackRenderContext context)
    {
        double x = this.x.get(context);
        double y = this.y.get(context);
        double z = this.z.get(context);
        PoseStack pose = context.pose();
        pose.translate(x, y, z);
    }
}
