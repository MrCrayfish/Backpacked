package com.mrcrayfish.backpacked.common.challenge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public abstract class ChallengeSerializer<T extends Challenge>
{
    public static final Codec<ChallengeSerializer<?>> CODEC = ResourceLocation.CODEC.flatXmap(id -> {
        ChallengeSerializer<?> serializer = ChallengeManager.instance().getSerializer(id);
        return serializer != null ? DataResult.success(serializer) : DataResult.error(() -> "Serializer does not exist");
    }, serializer -> {
        ResourceLocation id = ChallengeManager.instance().getSerializerId(serializer);
        return id != null ? DataResult.success(id) : DataResult.error(() -> "Unregistered serializer");
    });

    public abstract Codec<T> codec();
}
