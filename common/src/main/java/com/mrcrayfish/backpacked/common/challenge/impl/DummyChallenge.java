package com.mrcrayfish.backpacked.common.challenge.impl;

import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public final class DummyChallenge extends Challenge
{
    public static final DummyChallenge INSTANCE = new DummyChallenge();
    public static final MapCodec<DummyChallenge> CODEC = MapCodec.unit(INSTANCE);

    private DummyChallenge()
    {
        super();
    }

    @Override
    public ChallengeSerializer<?> getSerializer()
    {
        return new ChallengeSerializer<>(Utils.rl("dummy"), DummyChallenge.CODEC);
    }

    @Override
    public IProgressTracker createProgressTracker(ProgressFormatter formatter, ResourceLocation backpackId)
    {
        return null;
    }
}
