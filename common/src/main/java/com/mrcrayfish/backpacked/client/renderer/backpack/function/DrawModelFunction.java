package com.mrcrayfish.backpacked.client.renderer.backpack.function;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.platform.ClientServices;
import com.mrcrayfish.framework.api.client.FrameworkClientAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public record DrawModelFunction(ModelResourceLocation model, Optional<Vector3f> origin) implements BaseFunction
{
    public static final Type TYPE = new Type(
        ResourceLocation.withDefaultNamespace("draw_model"),
        RecordCodecBuilder.<DrawModelFunction>mapCodec(builder -> builder.group(
            ResourceLocation.CODEC.fieldOf("model").xmap(FrameworkClientAPI::createModelResourceLocation, ModelResourceLocation::id).forGetter(o -> o.model),
            ExtraCodecs.VECTOR3F.optionalFieldOf("origin").forGetter(o -> o.origin)
        ).apply(builder, DrawModelFunction::new)));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public void apply(BackpackRenderContext context)
    {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(this.model);
        context.renderer().draw(model);
    }
}
