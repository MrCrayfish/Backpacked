package com.mrcrayfish.backpacked.common.challenge;

import com.mrcrayfish.backpacked.Constants;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class ChallengeUtils
{
    public static boolean testPredicate(Optional<BlockPredicate> optional, BlockState state, @Nullable CompoundTag tag)
    {
        if(optional.isEmpty())
            return true;
        BlockPredicate predicate = optional.get();
        if(predicate.tag().isPresent() && !state.is(predicate.tag().get()))
            return false;
        if(predicate.blocks().isPresent() && !state.is(predicate.blocks().get()))
            return false;
        if(tag != null && predicate.nbt().isPresent() && !predicate.nbt().get().matches(tag)) {
            return false;
        }
        return predicate.properties().isEmpty() || predicate.properties().get().matches(state);
    }

    public static boolean testPredicate(Optional<ItemPredicate> optional, ItemStack stack)
    {
        if(optional.isEmpty())
            return true;
        ItemPredicate predicate = optional.get();
        return predicate.matches(stack);
    }

    public static void writeBlockPredicate(FriendlyByteBuf buf, Optional<BlockPredicate> optional)
    {
        buf.writeOptional(optional, (buf1, predicate) -> {
            buf1.writeNbt(BlockPredicate.CODEC.encodeStart(NbtOps.INSTANCE, predicate)
                .getOrThrow(false, Constants.LOG::error));
        });
    }

    public static Optional<BlockPredicate> readBlockPredicate(FriendlyByteBuf buf)
    {
        return buf.readOptional(buf1 -> BlockPredicate.CODEC
            .parse(NbtOps.INSTANCE, buf1.readNbt(NbtAccounter.create(2097152L)))
            .getOrThrow(false, Constants.LOG::error)
        );
    }

    public static void writeItemPredicate(FriendlyByteBuf buf, Optional<ItemPredicate> optional)
    {
        buf.writeOptional(optional, (buf1, predicate) -> {
            buf1.writeNbt(ItemPredicate.CODEC.encodeStart(NbtOps.INSTANCE, predicate)
                .getOrThrow(false, Constants.LOG::error));
        });
    }

    public static Optional<ItemPredicate> readItemPredicate(FriendlyByteBuf buf)
    {
        return buf.readOptional(buf1 -> ItemPredicate.CODEC
            .parse(NbtOps.INSTANCE, buf1.readNbt(NbtAccounter.create(2097152L)))
            .getOrThrow(false, Constants.LOG::error)
        );
    }

    public static void writeEntityPredicate(FriendlyByteBuf buf, Optional<EntityPredicate> optional)
    {
        buf.writeOptional(optional, (buf1, predicate) -> {
            buf1.writeNbt(EntityPredicate.CODEC.encodeStart(NbtOps.INSTANCE, predicate)
                .getOrThrow(false, Constants.LOG::error));
        });
    }

    public static Optional<EntityPredicate> readEntityPredicate(FriendlyByteBuf buf)
    {
        return buf.readOptional(buf1 -> EntityPredicate.CODEC
            .parse(NbtOps.INSTANCE, buf1.readNbt(NbtAccounter.create(2097152L)))
            .getOrThrow(false, Constants.LOG::error)
        );
    }
}
