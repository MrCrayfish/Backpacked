package com.mrcrayfish.backpacked.client.renderer.backpack.function;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.value.Value;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public record RotateFunction(Value x, Value y, Value z) implements BaseFunction
{
    public static final Type TYPE = new Type(new ResourceLocation("rotate"), RecordCodecBuilder.<RotateFunction>create(builder -> {
        return builder.group(
            Value.CODEC.fieldOf("x").orElse(Value.ZERO).forGetter(o -> o.x),
            Value.CODEC.fieldOf("y").orElse(Value.ZERO).forGetter(o -> o.y),
            Value.CODEC.fieldOf("z").orElse(Value.ZERO).forGetter(o -> o.z)
        ).apply(builder, RotateFunction::new);
    }));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public void apply(BackpackRenderContext context)
    {
        double x = this.x.getValue(context);
        double y = this.y.getValue(context);
        double z = this.z.getValue(context);
        PoseStack pose = context.pose();
        pose.mulPose(Axis.XP.rotationDegrees((float) x));
        pose.mulPose(Axis.YP.rotationDegrees((float) y));
        pose.mulPose(Axis.ZP.rotationDegrees((float) z));
    }
}
