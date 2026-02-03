package com.mrcrayfish.backpacked.common.augment;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public sealed class SavedAugments permits SavedAugments.View
{
    private final HashMap<AugmentType<?>, Augment<?>> map;

    public SavedAugments(Map<AugmentType<?>, Augment<?>> map)
    {
        this.map = new HashMap<>(map);
    }

    public SavedAugments()
    {
        this(new HashMap<>());
    }

    public void add(Augment<?> augment)
    {
        this.map.put(augment.type(), augment);
    }

    public void remove(Augment<?> augment)
    {
        this.map.remove(augment.type());
    }

    public Augment<?> getSavedOrCreateDefault(AugmentType<?> type)
    {
        return this.map.getOrDefault(type, type.defaultSupplier().get());
    }

    public static SavedAugments get(ItemStack stack)
    {
        return new View(stack);
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == this) return true;
        if(obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SavedAugments) obj;
        return Objects.equals(this.map, that.map);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.map);
    }

    public static final class View extends SavedAugments
    {
        private static final String KEY = "BackpackedSavedAugments";

        private final ItemStack stack;

        public View(ItemStack stack)
        {
            this.stack = stack;
        }

        @Nullable
        private CompoundTag getTag()
        {
            CompoundTag tag = this.stack.getOrCreateTag();
            if(tag.contains(KEY, Tag.TAG_COMPOUND))
            {
                return tag.getCompound(KEY);
            }
            return null;
        }

        private CompoundTag getOrCreateTag()
        {
            CompoundTag tag = this.stack.getOrCreateTag();
            if(!tag.contains(KEY, Tag.TAG_COMPOUND))
            {
                tag.put(KEY, new CompoundTag());
            }
            return tag.getCompound(KEY);
        }

        @Nullable
        private Augment<?> get(AugmentType<?> type)
        {
            CompoundTag tag = this.getTag();
            if(tag == null)
                return null;

            ListTag augmentList = tag.getList("Augments", Tag.TAG_COMPOUND);
            for(Tag nbt : augmentList)
            {
                CompoundTag augmentTag = (CompoundTag) nbt;
                if(augmentTag.getString("Id").equals(type.id().toString()))
                {
                    return Augment.CODEC.parse(NbtOps.INSTANCE, augmentTag.getCompound("Value")).result().orElse(null);
                }
            }
            return null;
        }

        private void remove(AugmentType<?> type)
        {
            CompoundTag tag = this.getTag();
            if(tag == null)
                return;
            ListTag augmentList = tag.getList("Augments", Tag.TAG_COMPOUND);
            augmentList.removeIf(tag1 -> {
                CompoundTag augmentTag = (CompoundTag) tag1;
                return augmentTag.getString("Id").equals(type.id().toString());
            });
        }

        @Override
        public void add(Augment<?> augment)
        {
            Augment.CODEC.encodeStart(NbtOps.INSTANCE, augment).result().ifPresent(nbt -> {
                this.remove(augment.type()); // Remove possible duplicates
                CompoundTag tag = this.getOrCreateTag();
                ListTag augmentList = tag.getList("Augments", Tag.TAG_COMPOUND);
                CompoundTag augmentTag = new CompoundTag();
                augmentTag.putString("Id", augment.type().id().toString());
                augmentTag.put("Value", nbt);
                augmentList.add(augmentTag);
                tag.put("Augments", augmentList);
            });
        }

        @Override
        public void remove(Augment<?> augment)
        {
            this.remove(augment.type());
        }

        @Override
        public Augment<?> getSavedOrCreateDefault(AugmentType<?> type)
        {
            Augment<?> augment = this.get(type);
            return augment != null ? augment : type.defaultSupplier().get();
        }
    }
}
