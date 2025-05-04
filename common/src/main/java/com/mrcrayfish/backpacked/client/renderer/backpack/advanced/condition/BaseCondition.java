package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import net.minecraft.resources.ResourceLocation;

public interface BaseCondition
{
    Codec<BaseCondition> CODEC = BaseCondition.Type.CODEC.dispatch(BaseCondition::type, BaseCondition.Type::codec);

    Type type();

    boolean test(BackpackRenderContext context);

    record Type(ResourceLocation id, MapCodec<? extends BaseCondition> codec)
    {
        public static final Codec<BaseCondition.Type> CODEC = ResourceLocation.CODEC.flatXmap(id -> {
            BaseCondition.Type serializer = ConditionTypes.getAll().get(id);
            if(serializer != null) {
                return DataResult.success(serializer);
            }
            return DataResult.error(() -> "Unregistered condition: " + id);
        }, function -> {
            if(ConditionTypes.getAll().containsKey(function.id)) {
                return DataResult.success(function.id);
            }
            return DataResult.error(() -> "Unregistered condition: " + function.id);
        });
    }
}
