package com.mrcrayfish.backpacked.common.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.function.BaseFunction;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public record ModelMeta(Vector3f shelfOffset, Optional<ItemTransform> guiDisplay, Optional<List<BaseFunction>> renderer)
{
    public static final Codec<ItemTransform> ITEM_TRANSFORM_CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
            ExtraCodecs.VECTOR3F.fieldOf("rotation").orElseGet(Vector3f::new).forGetter(o -> o.rotation),
            ExtraCodecs.VECTOR3F.fieldOf("translation").orElseGet(Vector3f::new).xmap(vector3f -> {
                return vector3f.mul(0.0625F);
            }, Function.identity()).forGetter(o -> o.translation),
            ExtraCodecs.VECTOR3F.fieldOf("scale").orElseGet(() -> new Vector3f(1, 1, 1)).forGetter(o -> o.scale)
        ).apply(builder, ItemTransform::new);
    });
    public static final ModelMeta DEFAULT = new ModelMeta(new Vector3f(), Optional.empty(), Optional.empty());
    public static final Codec<ModelMeta> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        ExtraCodecs.strictOptionalField(ExtraCodecs.VECTOR3F, "shelf_offset", new Vector3f()).forGetter(o -> o.shelfOffset),
        ExtraCodecs.strictOptionalField(ITEM_TRANSFORM_CODEC, "gui_display").forGetter(o -> o.guiDisplay),
        ExtraCodecs.strictOptionalField(BaseFunction.CODEC.listOf(), "renderer").forGetter(o -> o.renderer)
    ).apply(builder, ModelMeta::new));

}
