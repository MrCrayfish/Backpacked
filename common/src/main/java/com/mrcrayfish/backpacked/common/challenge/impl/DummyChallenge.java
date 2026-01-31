package com.mrcrayfish.backpacked.common.challenge.impl;

import com.google.gson.JsonObject;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.challenge.Challenge;
import com.mrcrayfish.backpacked.common.challenge.ChallengeSerializer;
import com.mrcrayfish.backpacked.common.tracker.IProgressTracker;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public final class DummyChallenge extends Challenge
{
    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "dummy");
    public static final DummyChallenge INSTANCE = new DummyChallenge();
    public static final Serializer SERIALIZER = new Serializer();

    private DummyChallenge()
    {
        super(ID);
    }

    @Override
    public ChallengeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    public IProgressTracker createProgressTracker(ProgressFormatter formatter, ResourceLocation backpackId)
    {
        return null;
    }

    public static class Serializer extends ChallengeSerializer<DummyChallenge>
    {
        @Override
        public DummyChallenge deserialize(JsonObject object)
        {
            return INSTANCE;
        }
    }
}
