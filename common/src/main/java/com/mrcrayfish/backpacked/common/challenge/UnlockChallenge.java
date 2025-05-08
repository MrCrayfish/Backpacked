package com.mrcrayfish.backpacked.common.challenge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.challenge.impl.DummyChallenge;
import com.mrcrayfish.backpacked.common.tracker.ProgressFormatter;

public record UnlockChallenge(ProgressFormatter formatter, Challenge challenge)
{
    public static final Codec<UnlockChallenge> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        ProgressFormatter.CODEC.fieldOf("formatter").orElse(ProgressFormatter.INCOMPLETE_COMPLETE).forGetter(o -> o.formatter),
        Challenge.CODEC.fieldOf("challenge").forGetter(o -> o.challenge)
    ).apply(builder, UnlockChallenge::new));
    public static final UnlockChallenge DUMMY = new UnlockChallenge(ProgressFormatter.INCOMPLETE_COMPLETE, DummyChallenge.INSTANCE);
}
