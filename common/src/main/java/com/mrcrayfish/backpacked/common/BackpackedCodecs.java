package com.mrcrayfish.backpacked.common;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mrcrayfish.backpacked.Config;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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

    public static final Codec<NonNullList<ItemStack>> BACKPACK_LIST = ItemStack.OPTIONAL_CODEC.sizeLimitedListOf(Config.MAX_EQUIPPABLE_BACKPACKS).xmap(list -> {
        NonNullList<ItemStack> items = NonNullList.withSize(list.size(), ItemStack.EMPTY);
        for(int i = 0; i < list.size(); i++) {
            ItemStack stack = list.get(i);
            items.set(i, stack == null ? ItemStack.EMPTY : stack);
        }
        return items;
    }, Function.identity());
}
