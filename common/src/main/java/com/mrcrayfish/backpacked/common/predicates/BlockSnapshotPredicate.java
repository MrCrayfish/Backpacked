package com.mrcrayfish.backpacked.common.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.BlockSnapshot;
import com.mrcrayfish.backpacked.common.challenge.PredicateUtils;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;

/**
 * Predicate to test block states that were removed from a level.
 */
public record BlockSnapshotPredicate(Optional<BlockPredicate> block, Optional<BlockPositionPredicate> position, Optional<HolderSet<Biome>> biomes, Optional<HolderSet<Structure>> structures, Optional<ResourceKey<Level>> dimension)
{
    public static final Codec<BlockSnapshotPredicate> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        BlockPredicate.CODEC.optionalFieldOf("block").forGetter(BlockSnapshotPredicate::block),
        BlockPositionPredicate.CODEC.optionalFieldOf("position").forGetter(BlockSnapshotPredicate::position),
        RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biomes").forGetter(BlockSnapshotPredicate::biomes),
        RegistryCodecs.homogeneousList(Registries.STRUCTURE).optionalFieldOf("structures").forGetter(BlockSnapshotPredicate::structures),
        ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("dimension").forGetter(BlockSnapshotPredicate::dimension)
    ).apply(builder, BlockSnapshotPredicate::new));

    /**
     * Tests a removed block that was recently removed from the world
     *
     * @param snapshot the level the block was removed from
     *
     * @return True if the removed block matches the predicate rules
     */
    public boolean test(BlockSnapshot snapshot)
    {
        if(!PredicateUtils.match(this.block, snapshot.state(), snapshot.tag()))
            return false;
        if(!this.position.map(predicate -> predicate.test(snapshot.pos())).orElse(true))
            return false;
        if(!this.biomes.map(set -> set.contains(snapshot.biome())).orElse(true))
            return false;
        if(!this.structures.map(holders -> snapshot.level().structureManager().getStructureWithPieceAt(snapshot.pos(), holders).isValid()).orElse(true))
            return false;
        return this.dimension.map(key -> key == snapshot.level().dimension()).orElse(true);
    }
}
