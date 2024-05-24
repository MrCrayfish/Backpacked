package com.mrcrayfish.backpacked.client.renderer.backpack.value.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public record StaticSource(double value) implements BaseSource
{
    public static final Type TYPE = new Type(new ResourceLocation("static"), RecordCodecBuilder.<StaticSource>create(builder ->
        builder.group(Codec.DOUBLE.fieldOf("value").forGetter(o -> o.value)
    ).apply(builder, StaticSource::new)));

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
