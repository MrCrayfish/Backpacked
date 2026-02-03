package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.platform.ClientServices;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public record DrawModelFunction(ResourceLocation model, Optional<Vector3f> origin) implements BaseFunction
{
    public static final Type TYPE = new Type(
        Utils.rl("draw_model"),
        RecordCodecBuilder.<DrawModelFunction>create(builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("model").forGetter(o -> o.model),
            ExtraCodecs.VECTOR3F.optionalFieldOf("origin").forGetter(o -> o.origin)
        ).apply(builder, DrawModelFunction::new))
    );

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public void apply(BackpackRenderContext context)
    {
        if(context.renderMode().canDrawModels())
        {
            BakedModel model = ClientServices.CLIENT.getBakedModel(this.model);
            context.bakedModelRenderer().accept(model);
        }
    }
}
