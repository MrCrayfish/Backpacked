package com.mrcrayfish.backpacked.common.challenge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public record ChallengeSerializer<T extends Challenge>(ResourceLocation id, MapCodec<T> codec)
{
    public static final Codec<ChallengeSerializer<?>> CODEC = ResourceLocation.CODEC.flatXmap(id -> {
        ChallengeSerializer<?> serializer = ChallengeManager.instance().getSerializer(id);
        return serializer != null ? DataResult.success(serializer) : DataResult.error(() -> "The challenge type '" + id + "' does not exist");
    }, serializer -> {
        ResourceLocation id = ChallengeManager.instance().getSerializerId(serializer);
        return id != null ? DataResult.success(id) : DataResult.error(() -> "Unregistered challenge type: " + serializer.id);
    });
}
