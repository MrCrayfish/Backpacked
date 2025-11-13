package com.mrcrayfish.backpacked.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Predicate;

public record ItemCollection(HashSet<ResourceLocation> ids)
{
    public static final ItemCollection EMPTY = new ItemCollection(new HashSet<>());

    public static final Codec<ItemCollection> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
        ResourceLocation.CODEC.sizeLimitedListOf(64).xmap(HashSet::new, ArrayList::new).fieldOf("filters").orElse(new HashSet<>()).forGetter(f -> f.ids)
    ).apply(instance, ItemCollection::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemCollection> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)), ItemCollection::ids,
        ItemCollection::new
    );

    private static final ResourceLocation AIR = ResourceLocation.withDefaultNamespace("air");

    public ItemCollection
    {
        ids.removeIf(id -> !BuiltInRegistries.ITEM.containsKey(id) || id.equals(AIR));
    }

    public boolean has(Item item)
    {
        var id = BuiltInRegistries.ITEM.getKey(item);
        return this.ids.contains(id);
    }

    public ItemCollection add(Item item)
    {
        HashSet<ResourceLocation> filters = new HashSet<>(this.ids);
        filters.add(BuiltInRegistries.ITEM.getKey(item));
        return new ItemCollection(filters);
    }

    public ItemCollection remove(Item item)
    {
        HashSet<ResourceLocation> filters = new HashSet<>(this.ids);
        filters.remove(BuiltInRegistries.ITEM.getKey(item));
        return new ItemCollection(filters);
    }

    public ItemCollection filter(Predicate<Item> predicate)
    {
        HashSet<ResourceLocation> filters = new HashSet<>(this.ids);
        filters.removeIf(id -> !predicate.test(BuiltInRegistries.ITEM.get(id)));
        return new ItemCollection(filters);
    }

}
