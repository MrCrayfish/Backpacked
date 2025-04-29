package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public record ConstantSource(double value) implements BaseSource
{
    public static final Type TYPE = new Type(ResourceLocation.withDefaultNamespace("constant"), RecordCodecBuilder.<ConstantSource>mapCodec(builder ->
        builder.group(Codec.DOUBLE.fieldOf("value").forGetter(o -> o.value)
    ).apply(builder, ConstantSource::new)));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public double apply(BackpackRenderContext context)
    {
        return this.value;
    }
}
