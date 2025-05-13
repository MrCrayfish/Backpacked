package com.mrcrayfish.backpacked.common.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;

public record BlockPositionPredicate(MinMaxBounds.Ints x, MinMaxBounds.Ints y, MinMaxBounds.Ints z)
{
    public static final Codec<BlockPositionPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        MinMaxBounds.Ints.CODEC.optionalFieldOf("x", MinMaxBounds.Ints.ANY).forGetter(BlockPositionPredicate::x),
        MinMaxBounds.Ints.CODEC.optionalFieldOf("y", MinMaxBounds.Ints.ANY).forGetter(BlockPositionPredicate::y),
        MinMaxBounds.Ints.CODEC.optionalFieldOf("z", MinMaxBounds.Ints.ANY).forGetter(BlockPositionPredicate::z)
    ).apply(builder, BlockPositionPredicate::new));

    public boolean test(BlockPos pos)
    {
        return this.x.matches(pos.getX()) && this.y.matches(pos.getY()) && this.z.matches(pos.getZ());
    }
}
