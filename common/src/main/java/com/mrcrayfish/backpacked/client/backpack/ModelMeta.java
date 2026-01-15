package com.mrcrayfish.backpacked.client.backpack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderer;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public record ModelMeta(Vector3f shelfOffset, ItemTransforms display, Optional<BackpackRenderer> renderer, boolean bobbing)
{
    public static final Codec<ItemTransform> ITEM_TRANSFORM_CODEC = RecordCodecBuilder.create(builder -> builder.group(
        ExtraCodecs.VECTOR3F.fieldOf("rotation").orElseGet(Vector3f::new).forGetter(o -> o.rotation),
        ExtraCodecs.VECTOR3F.fieldOf("translation").orElseGet(Vector3f::new).xmap(vector3f -> {
            return vector3f.mul(0.0625F);
        }, Function.identity()).forGetter(o -> o.translation),
        ExtraCodecs.VECTOR3F.fieldOf("scale").orElseGet(() -> new Vector3f(1, 1, 1)).forGetter(o -> o.scale)
    ).apply(builder, ItemTransform::new));

    public static final Codec<ItemTransforms> ITEM_TRANSFORMS_CODEC = RecordCodecBuilder.create(builder -> builder.group(
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.THIRD_PERSON_LEFT_HAND.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(o -> o.thirdPersonLeftHand),
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(o -> o.thirdPersonRightHand),
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.FIRST_PERSON_LEFT_HAND.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(o -> o.firstPersonLeftHand),
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(o -> o.firstPersonRightHand),
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.HEAD.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(o -> o.head),
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.GUI.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(o -> o.gui),
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.GROUND.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(o -> o.ground),
        ITEM_TRANSFORM_CODEC.optionalFieldOf(ItemDisplayContext.FIXED.getSerializedName(), ItemTransform.NO_TRANSFORM).forGetter(o -> o.fixed)
    ).apply(builder, ItemTransforms::new));

    public static final ModelMeta DEFAULT = new ModelMeta(new Vector3f(), ItemTransforms.NO_TRANSFORMS, Optional.empty(), true);

    public static final Codec<ModelMeta> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        ExtraCodecs.VECTOR3F.optionalFieldOf("shelf_offset", new Vector3f()).forGetter(o -> o.shelfOffset),
        ITEM_TRANSFORMS_CODEC.optionalFieldOf("display", ItemTransforms.NO_TRANSFORMS).forGetter(o -> o.display),
        BackpackRenderer.CODEC.optionalFieldOf("renderer").forGetter(o -> o.renderer),
        Codec.BOOL.optionalFieldOf("bobbing", true).forGetter(o -> o.bobbing)
    ).apply(builder, ModelMeta::new));

    public ItemTransform getItemTransform(ItemDisplayContext context)
    {
        if(context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND && this.display.firstPersonLeftHand == ItemTransform.NO_TRANSFORM)
            return this.display.firstPersonRightHand;
        if(context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND && this.display.thirdPersonLeftHand == ItemTransform.NO_TRANSFORM)
            return this.display.thirdPersonRightHand;
        return this.display.getTransform(context);
    }
}
