package com.mrcrayfish.backpacked.common.augment;

import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

public record AugmentType<T extends Augment<T>>(ResourceLocation id, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Supplier<T> defaultSupplier, ResourceLocation sprite, Component name)
{
    static final Map<ResourceLocation, AugmentType<?>> REGISTRY = HashBiMap.create();
    static final Codec<AugmentType<?>> CODEC = ResourceLocation.CODEC.flatXmap(id -> {
        AugmentType<?> type = REGISTRY.get(id);
        if(type != null)
            return DataResult.success(type);
        return DataResult.error(() -> "Unknown augment type: " + id);
    }, type -> {
        if(REGISTRY.containsKey(type.id))
            return DataResult.success(type.id);
        return DataResult.error(() -> "Unregistered augment type: " + type.id);
    });

    public AugmentType(ResourceLocation id, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Supplier<T> defaultSupplier)
    {
        this(id, codec, streamCodec, defaultSupplier,
            ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "augment/%s".formatted(id.getPath())),
            Component.translatable("augment.%s.%s".formatted(id.getNamespace(), id.getPath().replace("/", ".")))
        );
    }

    public static <T extends Augment<T>> void register(final AugmentType<T> type)
    {
        synchronized (REGISTRY)
        {
            AugmentType<?> old = REGISTRY.put(type.id(), type);
            if(old != null)
            {
                throw new IllegalArgumentException("Duplicate augment: " + old.id());
            }
        }
    }

    public static Collection<AugmentType<?>> all()
    {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }
}
