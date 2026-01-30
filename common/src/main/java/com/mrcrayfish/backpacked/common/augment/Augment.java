package com.mrcrayfish.backpacked.common.augment;

import com.mojang.serialization.Codec;
import com.mrcrayfish.backpacked.core.ModRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;
 // TODO DONE
@SuppressWarnings("unchecked")
public interface Augment<T extends Augment<T>>
{
    Codec<Augment<?>> CODEC = AugmentType.CODEC.dispatch(Augment::type, AugmentType::codec);

    AugmentType<T> type();

    default T onUpdate(ServerPlayer player, Augment<?> current)
    {
        return (T) this;
    }

    static <T> BiConsumer<FriendlyByteBuf, T> noOp()
    {
        return (buf, t) -> {};
    }

    static <T extends Augment<T>> Function<FriendlyByteBuf, T> unit(T instance)
    {
        return buf -> instance;
    }

    static void encode(FriendlyByteBuf buf, Augment<?> augment)
    {
        buf.writeResourceLocation(augment.type().id());
        ((BiConsumer<FriendlyByteBuf, Augment<?>>) augment.type().encoder()).accept(buf, augment);
    }

    static Augment<?> decode(FriendlyByteBuf buf)
    {
        ResourceLocation id = buf.readResourceLocation();
        AugmentType<?> type = ModRegistries.AUGMENT_TYPES.getValue(id);
        if(type == null)
            throw new IllegalStateException("Unknown augment type " + id);
        return type.decoder().apply(buf);
    }
}
