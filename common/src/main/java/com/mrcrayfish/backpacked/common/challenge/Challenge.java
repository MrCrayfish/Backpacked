package com.mrcrayfish.backpacked.common.challenge;

import com.mojang.serialization.Codec;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public abstract class Challenge
{
    public static final Codec<Challenge> CODEC = ChallengeSerializer.CODEC.dispatch(Challenge::getSerializer, ChallengeSerializer::codec);

    private final ResourceLocation id;

    protected Challenge(ResourceLocation id)
    {
        this.id = id;
    }

    public abstract ChallengeSerializer<?> getSerializer();

    public abstract IProgressTracker createProgressTracker(ResourceLocation backpackId);
}
