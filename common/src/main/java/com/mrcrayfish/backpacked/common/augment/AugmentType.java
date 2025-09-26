package com.mrcrayfish.backpacked.common.augment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mrcrayfish.backpacked.core.ModRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public record AugmentType<T extends Augment<T>>(ResourceLocation id, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, Supplier<T> defaultSupplier, ResourceLocation sprite, Component name) implements Comparable<AugmentType<?>>
{
    public static final Comparator<AugmentType<?>> BY_NAME = Comparator.comparing(type -> type.name().getString());
    static final Codec<AugmentType<?>> CODEC = ResourceLocation.CODEC.flatXmap(id -> {
        AugmentType<?> type = ModRegistries.AUGMENT_TYPES.getValue(id);
        if(type != null)
            return DataResult.success(type);
        return DataResult.error(() -> "Unknown augment type: " + id);
    }, type -> {
        if(ModRegistries.AUGMENT_TYPES.containsKey(type.id))
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

    public static Stream<AugmentType<? extends Augment<?>>> stream()
    {
        return StreamSupport.stream(ModRegistries.AUGMENT_TYPES.spliterator(), false);
    }

    @Override
    public int compareTo(@NotNull AugmentType<?> other)
    {
        return BY_NAME.compare(this, other);
    }
}
