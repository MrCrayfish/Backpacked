package com.mrcrayfish.backpacked.common.augment;

import com.mojang.serialization.Codec;
import com.mrcrayfish.backpacked.core.ModRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings("unchecked")
public interface Augment<T extends Augment<T>>
{
    Codec<Augment<?>> CODEC = AugmentType.CODEC.dispatch(Augment::type, AugmentType::codec);
    StreamCodec<RegistryFriendlyByteBuf, Augment<?>> STREAM_CODEC = StreamCodec.of((buf, augment) -> {
        ResourceLocation.STREAM_CODEC.encode(buf, augment.type().id());
        ((StreamCodec<RegistryFriendlyByteBuf, Augment<?>>) augment.type().streamCodec()).encode(buf, augment);
    }, buf -> {
        ResourceLocation id = buf.readResourceLocation();
        AugmentType<?> type = ModRegistries.AUGMENT_TYPES.getValue(id);
        if(type == null)
            throw new IllegalStateException("Unknown augment type " + id);
        return type.streamCodec().decode(buf);
    });

    AugmentType<T> type();

    default T onUpdate(ServerPlayer player, Augment<?> current)
    {
        return (T) this;
    }
}
