package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.StandaloneModels;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.client.model.FrameworkBakedModel;
import com.mrcrayfish.framework.api.client.model.FrameworkModelResource;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3fc;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public record DrawModelFunction(Identifier model, Optional<Vector3fc> origin) implements BaseFunction
{
    public static final Type TYPE = new Type(
        Utils.id("draw_model"),
        RecordCodecBuilder.<DrawModelFunction>mapCodec(builder -> builder.group(
            Identifier.CODEC.fieldOf("model").forGetter(o -> o.model),
            ExtraCodecs.VECTOR3F.optionalFieldOf("origin").forGetter(DrawModelFunction::origin)
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
            FrameworkModelResource<FrameworkBakedModel> resource = StandaloneModels.getResource(this.model);
            if(resource != null)
            {
                FrameworkBakedModel model = resource.getModel();
                if(model != null)
                {
                    context.modelRenderer().accept(resource.getModel());
                }
            }
        }
    }
}
