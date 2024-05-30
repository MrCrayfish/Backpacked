package com.mrcrayfish.backpacked.common;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;

/**
 * Author: MrCrayfish
 */
public class BackpackedCodecs
{
    public static final Codec<ImmutableList<EntityType<?>>> ENTITY_TYPE_LIST = Codec
        .either(BuiltInRegistries.ENTITY_TYPE.byNameCodec().listOf(), BuiltInRegistries.ENTITY_TYPE.byNameCodec()).xmap(
            either -> either.map(ImmutableList::copyOf, ImmutableList::of),
            list -> list.size() == 1 ? Either.right(list.get(0)) : Either.left(list)
        );
}
