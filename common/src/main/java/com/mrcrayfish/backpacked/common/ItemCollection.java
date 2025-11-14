package com.mrcrayfish.backpacked.common;

import com.google.common.base.Preconditions;
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
import java.util.Iterator;
import java.util.function.Predicate;

public record ItemCollection(HashSet<ResourceLocation> ids)
{
    public static final ItemCollection EMPTY = new ItemCollection(new HashSet<>());
    public static final int MAX_ENTRIES = 256;

    public static final Codec<ItemCollection> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
        ResourceLocation.CODEC.sizeLimitedListOf(MAX_ENTRIES).xmap(HashSet::new, ArrayList::new).fieldOf("ids").orElse(new HashSet<>()).forGetter(f -> f.ids)
    ).apply(instance, ItemCollection::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemCollection> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)), ItemCollection::ids,
        ItemCollection::new
    );

    private static final ResourceLocation AIR = ResourceLocation.withDefaultNamespace("air");

    public ItemCollection
    {
        ids.removeIf(id -> !BuiltInRegistries.ITEM.containsKey(id) || id.equals(AIR));
        // Ensure set contains no more than max entries
        if(ids.size() > MAX_ENTRIES)
        {
            Iterator<ResourceLocation> it = ids.iterator();
            while(ids.size() > MAX_ENTRIES && it.hasNext())
            {
                it.remove();
            }
        }
    }

    public boolean has(Item item)
    {
        var id = BuiltInRegistries.ITEM.getKey(item);
        return this.ids.contains(id);
    }

    public ItemCollection add(Item item)
    {
        if(this.ids.size() >= MAX_ENTRIES)
            return this;
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

    public ItemCollection limit(int size)
    {
        Preconditions.checkArgument(size > 0, "size must be greater than zero");
        if(this.ids.size() <= size)
            return this;
        HashSet<ResourceLocation> set = new HashSet<>(this.ids);
        Iterator<ResourceLocation> it = set.iterator();
        while(set.size() > size && it.hasNext())
            it.remove();
        return new ItemCollection(set);
    }
}
