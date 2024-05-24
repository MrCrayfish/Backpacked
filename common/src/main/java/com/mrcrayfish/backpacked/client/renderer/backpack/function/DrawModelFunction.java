package com.mrcrayfish.backpacked.client.renderer.backpack.function;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.platform.ClientServices;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public record DrawModelFunction(ResourceLocation id, Optional<Vector3f> origin) implements BaseFunction
{
    public static final Type TYPE = new Type(new ResourceLocation("draw_model"), RecordCodecBuilder.<DrawModelFunction>create(builder -> {
        return builder.group(
            ResourceLocation.CODEC.fieldOf("model").forGetter(o -> o.id),
            ExtraCodecs.VECTOR3F.optionalFieldOf("origin").forGetter(o -> o.origin)
        ).apply(builder, DrawModelFunction::new);
    }));

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public void apply(BackpackRenderContext context)
    {
        BakedModel model = ClientServices.MODEL.getBakedModel(this.id);
        context.renderer().draw(model);
    }
}
