package com.mrcrayfish.backpacked.client.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderer;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Optional;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public record ModelMeta(Vector3fc shelfOffset, ItemTransforms display, Optional<BackpackRenderer> renderer, boolean bobbing)
{
    public static final Codec<ItemTransform> ITEM_TRANSFORM_CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
            ExtraCodecs.VECTOR3F.fieldOf("rotation").orElseGet(Vector3f::new).forGetter(ItemTransform::rotation),
            ExtraCodecs.VECTOR3F.fieldOf("translation").orElseGet(Vector3f::new).xmap(vector3f -> {
                return (Vector3fc) vector3f.mul(0.0625F, new Vector3f());
            }, Function.identity()).forGetter(ItemTransform::translation),
            ExtraCodecs.VECTOR3F.fieldOf("scale").orElseGet(() -> new Vector3f(1, 1, 1)).forGetter(ItemTransform::scale)
        ).apply(builder, ItemTransform::new);
    });

    public static final Codec<ItemTransforms> ITEM_TRANSFORMS_CODEC = RecordCodecBuilder.create(builder -> builder.group(
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.THIRD_PERSON_LEFT_HAND.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::thirdPersonLeftHand),
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::thirdPersonRightHand),
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.FIRST_PERSON_LEFT_HAND.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::firstPersonLeftHand),
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::firstPersonRightHand),
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.HEAD.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::head),
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.GUI.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::gui),
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.GROUND.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::ground),
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.FIXED.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::fixed),
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.ON_SHELF.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(ItemTransforms::fixedFromBottom)
    ).apply(builder, (thirdPersonLeftHand, thirdPersonRightHand, firstPersonLeftHand, firstPersonRightHand, head, gui, ground, fixed, shelf) -> {
        if(firstPersonLeftHand == ItemTransform.NO_TRANSFORM) firstPersonLeftHand = firstPersonRightHand;
        if(thirdPersonLeftHand == ItemTransform.NO_TRANSFORM) thirdPersonLeftHand = thirdPersonRightHand;
        return new ItemTransforms(thirdPersonLeftHand, thirdPersonRightHand, firstPersonLeftHand, firstPersonRightHand, head, gui, ground, fixed, shelf);
    }));

    public static final ModelMeta DEFAULT = new ModelMeta(new Vector3f(), ItemTransforms.NO_TRANSFORMS, Optional.empty(), true);

    public static final Codec<ModelMeta> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        ExtraCodecs.VECTOR3F.optionalFieldOf("shelf_offset", new Vector3f()).forGetter(o -> o.shelfOffset),
        ITEM_TRANSFORMS_CODEC.optionalFieldOf("display", ItemTransforms.NO_TRANSFORMS).forGetter(o -> o.display),
        BackpackRenderer.CODEC.optionalFieldOf("renderer").forGetter(o -> o.renderer),
        Codec.BOOL.optionalFieldOf("bobbing", true).forGetter(o -> o.bobbing)
    ).apply(builder, ModelMeta::new));
}
