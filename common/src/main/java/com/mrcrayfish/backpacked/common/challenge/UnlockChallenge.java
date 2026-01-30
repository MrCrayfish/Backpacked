package com.mrcrayfish.backpacked.common.challenge;

import com.mrcrayfish.backpacked.common.challenge.impl.DummyChallenge;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;

public record UnlockChallenge(ProgressFormatter formatter, Challenge challenge)
{
    public static final UnlockChallenge DUMMY = new UnlockChallenge(ProgressFormatter.INCOMPLETE_COMPLETE, DummyChallenge.INSTANCE);
}
