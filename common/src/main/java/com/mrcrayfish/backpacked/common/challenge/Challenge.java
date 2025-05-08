package com.mrcrayfish.backpacked.common.challenge;

import com.mojang.serialization.Codec;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public abstract class Challenge
{
    public static final Codec<Challenge> CODEC = ChallengeSerializer.CODEC.dispatch(Challenge::getSerializer, ChallengeSerializer::codec);

    protected Challenge() {}

    public abstract ChallengeSerializer<?> getSerializer();

    public abstract IProgressTracker createProgressTracker(ProgressFormatter formatter, ResourceLocation backpackId);
}
