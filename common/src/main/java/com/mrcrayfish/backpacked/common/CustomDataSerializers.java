package com.mrcrayfish.backpacked.common;

import com.mojang.serialization.DataResult;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.framework.api.sync.IDataSerializer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class CustomDataSerializers // TODO DONE
{
    public static final IDataSerializer<Optional<CosmeticProperties>> OPTIONAL_COSMETIC_PROPERTIES = new IDataSerializer<Optional<CosmeticProperties>>()
    {
        @Override
        public void write(FriendlyByteBuf buf, Optional<CosmeticProperties> value)
        {
            buf.writeOptional(value, (buf1, properties) -> properties.encode(buf1));
        }

        @Override
        public Optional<CosmeticProperties> read(FriendlyByteBuf buf)
        {
            return buf.readOptional(CosmeticProperties::decode);
        }

        @Override
        public Tag write(Optional<CosmeticProperties> value)
        {
            return new CompoundTag();
        }

        @Override
        public Optional<CosmeticProperties> read(Tag nbt)
        {
            return Optional.empty();
        }
    };

    public static final IDataSerializer<ItemStack> ITEM_STACK = new IDataSerializer<>()
    {
        @Override
        public void write(FriendlyByteBuf buf, ItemStack value)
        {
            buf.writeItem(value);
        }

        @Override
        public ItemStack read(FriendlyByteBuf buf)
        {
            return buf.readItem();
        }

        @Override
        public Tag write(ItemStack value)
        {
            return value.save(new CompoundTag());
        }

        @Override
        public ItemStack read(Tag nbt)
        {
            return ItemStack.of((CompoundTag) nbt);
        }
    };

    public static final IDataSerializer<NonNullList<ItemStack>> BACKPACKS = new IDataSerializer<>()
    {
        @Override
        public void write(FriendlyByteBuf buf, NonNullList<ItemStack> value)
        {
            buf.writeCollection(value, FriendlyByteBuf::writeItem);
        }

        @Override
        public NonNullList<ItemStack> read(FriendlyByteBuf buf)
        {
            return buf.readCollection(NonNullList::createWithCapacity, FriendlyByteBuf::readItem);
        }

        @Override
        public Tag write(NonNullList<ItemStack> value)
        {
            DataResult<Tag> result = ItemStack.CODEC.listOf().encodeStart(NbtOps.INSTANCE, value);
            return result.resultOrPartial(Constants.LOG::error).orElse(null);
        }

        @Override
        public NonNullList<ItemStack> read(Tag nbt)
        {
            DataResult<NonNullList<ItemStack>> result = BackpackedCodecs.BACKPACK_LIST.parse(NbtOps.INSTANCE, nbt);
            return result.resultOrPartial(Constants.LOG::error).orElse(NonNullList.withSize(Config.MAX_EQUIPPABLE_BACKPACKS, ItemStack.EMPTY));
        }
    };
}
