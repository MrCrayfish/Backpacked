package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.framework.api.sync.DataSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class CustomDataSerializers
{
    public static final DataSerializer<Optional<CosmeticProperties>> OPTIONAL_COSMETIC_PROPERTIES = new DataSerializer<>(
        CosmeticProperties.STREAM_CODEC.apply(ByteBufCodecs::optional),
        ExtraCodecs.optionalEmptyMap(CosmeticProperties.CODEC)
    );

    public static final DataSerializer<ItemStack> ITEM_STACK = new DataSerializer<>(
        ItemStack.OPTIONAL_STREAM_CODEC,
        ItemStack.OPTIONAL_CODEC
    );

    public static final DataSerializer<NonNullList<ItemStack>> BACKPACKS = new DataSerializer<>(
        ByteBufCodecs.collection(NonNullList::createWithCapacity, ItemStack.OPTIONAL_STREAM_CODEC, Config.MAX_EQUIPPABLE_BACKPACKS),
        BackpackedCodecs.BACKPACK_LIST
    );
}
