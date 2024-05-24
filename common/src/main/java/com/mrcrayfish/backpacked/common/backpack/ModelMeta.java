package com.mrcrayfish.backpacked.common.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.function.BaseFunction;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public record ModelMeta(Vector3f shelfOffset, Optional<List<BaseFunction>> renderer)
{
    public static final ModelMeta DEFAULT = new ModelMeta(new Vector3f(), Optional.empty());
    private static final Vector3f ZERO = new Vector3f();
    public static final Codec<ModelMeta> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        ExtraCodecs.VECTOR3F.optionalFieldOf("shelf_offset", ZERO).forGetter(o -> o.shelfOffset),
        BaseFunction.CODEC.listOf().optionalFieldOf("renderer").forGetter(o -> o.renderer)
    ).apply(builder, ModelMeta::new));
}
