package com.mrcrayfish.backpacked.common.predicates;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.common.BlockSnapshot;
import com.mrcrayfish.backpacked.common.challenge.PredicateUtils;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Predicate to test block states that were removed from a level.
 */ // TODO DONE
public record BlockSnapshotPredicate(Optional<BlockPredicate> block, Optional<BlockPositionPredicate> position, Optional<HolderSet<Biome>> biomes, Optional<HolderSet<Structure>> structures, Optional<ResourceKey<Level>> dimension)
{
    /**
     * Tests a removed block that was recently removed from the world
     *
     * @param snapshot the level the block was removed from
     *
     * @return True if the removed block matches the predicate rules
     */
    public boolean test(BlockSnapshot snapshot)
    {
        if(!PredicateUtils.testPredicate(this.block, snapshot))
            return false;
        if(!this.position.map(predicate -> predicate.test(snapshot.pos())).orElse(true))
            return false;
        if(!this.biomes.map(set -> set.contains(snapshot.biome())).orElse(true))
            return false;
        if(!this.structures.map(holders -> holders.stream().anyMatch(holder -> {
            return snapshot.level().structureManager().getStructureWithPieceAt(snapshot.pos(), holder.value()).isValid();
        })).orElse(true))
            return false;
        return this.dimension.map(key -> key == snapshot.level().dimension()).orElse(true);
    }

    public static BlockSnapshotPredicate fromJson(@Nullable JsonElement element)
    {
        Optional<BlockPredicate> block = Optional.empty();
        Optional<BlockPositionPredicate> position = Optional.empty();
        Optional<HolderSet<Biome>> biomes = Optional.empty();
        Optional<HolderSet<Structure>> structures = Optional.empty();
        Optional<ResourceKey<Level>> dimension = Optional.empty();
        if(element != null && !element.isJsonNull() && element.isJsonObject())
        {
            JsonObject root = element.getAsJsonObject();
            JsonObject blockObject = GsonHelper.getAsJsonObject(root, "block", null);
            if(blockObject != null)
            {
                block = Optional.of(BlockPredicate.fromJson(blockObject));
            }

            JsonElement positionElement = root.get("position");
            if(positionElement != null)
            {
                BlockPositionPredicate predicate = BlockPositionPredicate.fromJson(positionElement);
                position = Optional.of(predicate);
            }

            JsonElement biomesElement = root.get("biomes");
            if(biomesElement != null)
            {
                HolderSet<Biome> biomeSet = RegistryCodecs.homogeneousList(Registries.BIOME).parse(JsonOps.INSTANCE, biomesElement).result().orElseThrow();
                biomes = Optional.of(biomeSet);
            }

            JsonElement structuresElement = root.get("structures");
            if(structuresElement != null)
            {
                HolderSet<Structure> structureSet = RegistryCodecs.homogeneousList(Registries.STRUCTURE).parse(JsonOps.INSTANCE, biomesElement).result().orElseThrow();
                structures = Optional.of(structureSet);
            }

            JsonElement dimensionElement = root.get("structures");
            if(dimensionElement != null)
            {
                ResourceKey<Level> key = ResourceKey.codec(Registries.DIMENSION).parse(JsonOps.INSTANCE, dimensionElement).result().orElseThrow();
                dimension = Optional.of(key);
            }
        }
        return new BlockSnapshotPredicate(block, position, biomes, structures, dimension);
    }
}
