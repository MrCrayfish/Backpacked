package com.mrcrayfish.backpacked.common;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class BackpackedCodecs
{
    public static final Codec<Set<String>> STRING_SET = Codec.either(Codec.STRING, Codec.STRING.listOf()).xmap(either -> {
        return either.map(List::of, Function.identity());
    }, list -> {
        return list.size() == 1 ? Either.left(list.get(0)) : Either.right(list);
    }).xmap(HashSet::new, List::copyOf);

    public static final Codec<HolderSet<Item>> ITEMS = BuiltInRegistries.ITEM.holderByNameCodec().listOf().xmap(HolderSet::direct, set -> set.stream().toList());
}
