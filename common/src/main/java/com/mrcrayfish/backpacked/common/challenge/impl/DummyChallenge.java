package com.mrcrayfish.backpacked.common.challenge.impl;

import com.mojang.serialization.Codec;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public final class DummyChallenge extends Challenge
{
    public static final DummyChallenge INSTANCE = new DummyChallenge();
    public static final Codec<DummyChallenge> CODEC = Codec.unit(INSTANCE);

    private DummyChallenge()
    {
        super(new ResourceLocation(Constants.MOD_ID, "dummy"));
    }

    @Override
    public ChallengeSerializer<?> getSerializer()
    {
        return new ChallengeSerializer<DummyChallenge>()
        {

            @Override
            public Codec<DummyChallenge> codec()
            {
                return DummyChallenge.CODEC;
            }
        };
    }

    @Override
    public IProgressTracker createProgressTracker(ResourceLocation backpackId)
    {
        return null;
    }
}
