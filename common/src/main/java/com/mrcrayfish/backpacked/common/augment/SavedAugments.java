package com.mrcrayfish.backpacked.common.augment;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public record SavedAugments(Map<AugmentType<?>, Augment<?>> map)
{
    public static final Codec<SavedAugments> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(AugmentType.CODEC, Augment.CODEC).optionalFieldOf("map", new HashMap<>()).forGetter(SavedAugments::map)
    ).apply(instance, map -> new SavedAugments(ImmutableMap.copyOf(map))));

    public static final StreamCodec<RegistryFriendlyByteBuf, SavedAugments> STREAM_CODEC = StreamCodec.composite(
        Augment.STREAM_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)),
        a -> Collections.unmodifiableCollection(a.map().values()),
        SavedAugments::new
    );

    public SavedAugments()
    {
        this(ImmutableMap.of());
    }

    public SavedAugments(Collection<Augment<?>> list)
    {
        this(collectionToMap(list));
    }

    public SavedAugments add(Augment<?> augment)
    {
        Augment<?> current = this.map.get(augment.type());
        if(augment.equals(current))
            return this;

        ImmutableMap.Builder<AugmentType<?>, Augment<?>> builder = ImmutableMap.builder();
        builder.putAll(this.map);
        builder.put(augment.type(), augment);
        return new SavedAugments(builder.buildKeepingLast());
    }

    public SavedAugments remove(Augment<?> augment)
    {
        Augment<?> current = this.map.get(augment.type());
        if(current == null)
            return this;

        ImmutableMap.Builder<AugmentType<?>, Augment<?>> builder = ImmutableMap.builder();
        for(Map.Entry<AugmentType<?>, Augment<?>> entry : this.map.entrySet())
        {
            if(!entry.getKey().equals(current.type()))
            {
                builder.put(entry.getKey(), entry.getValue());
            }
        }
        return new SavedAugments(builder.buildKeepingLast());
    }

    public Augment<?> getSavedOrCreateDefault(AugmentType<?> type)
    {
        return this.map.getOrDefault(type, type.defaultSupplier().get());
    }

    private static ImmutableMap<AugmentType<?>, Augment<?>> collectionToMap(Collection<Augment<?>> augments)
    {
        ImmutableMap.Builder<AugmentType<?>, Augment<?>> builder = ImmutableMap.builder();
        augments.forEach(augment -> builder.put(augment.type(), augment));
        return builder.buildKeepingLast();
    }

    public static SavedAugments get(ItemStack stack)
    {
        return stack.getOrDefault(ModDataComponents.SAVED_AUGMENTS.get(), new SavedAugments());
    }

    public static void set(ItemStack stack, SavedAugments augments)
    {
        stack.set(ModDataComponents.SAVED_AUGMENTS.get(), augments);
    }
}
