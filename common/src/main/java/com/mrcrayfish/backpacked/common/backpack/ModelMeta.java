package com.mrcrayfish.backpacked.common.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

/**
 * Author: MrCrayfish
 */
public class ModelMeta
{
    public static final ModelMeta DEFAULT = new ModelMeta(new Vector3f());
    private static final Vector3f ZERO = new Vector3f();
    public static final Codec<ModelMeta> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(ExtraCodecs.VECTOR3F.optionalFieldOf("shelf_offset", ZERO)
            .forGetter(modelMeta -> {
                return modelMeta.shelfOffset;
            })).apply(builder, ModelMeta::new);
    });

    private final Vector3f shelfOffset;

    public ModelMeta(Vector3f shelfOffset)
    {
        this.shelfOffset = shelfOffset;
    }

    public Vector3f getShelfOffset()
    {
        return this.shelfOffset;
    }
}
