package com.mrcrayfish.backpacked.common.challenge;

import com.mrcrayfish.backpacked.common.BlockSnapshot;
import com.mrcrayfish.backpacked.common.predicates.BlockSnapshotPredicate;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class PredicateUtils
{
    public static boolean match(Optional<BlockPredicate> optional, BlockState state, @Nullable CompoundTag tag)
    {
        if(optional.isEmpty())
            return true;
        BlockPredicate predicate = optional.get();
        if(predicate.blocks().isPresent() && !state.is(predicate.blocks().get()))
            return false;
        if(tag != null && predicate.nbt().isPresent() && !predicate.nbt().get().matches(tag)) {
            return false;
        }
        return predicate.properties().isEmpty() || predicate.properties().get().matches(state);
    }

    public static boolean match(Optional<ItemPredicate> optional, ItemStack stack)
    {
        if(optional.isEmpty())
            return true;
        ItemPredicate predicate = optional.get();
        return predicate.test(stack);
    }

    public static boolean match(Optional<EntityPredicate> optional, ServerPlayer player, Entity entity)
    {
        if(optional.isEmpty())
            return true;
        EntityPredicate predicate = optional.get();
        return predicate.matches(player, entity);
    }

    public static boolean match(Optional<EntityPredicate> optional, ServerPlayer player)
    {
        if(optional.isEmpty())
            return true;
        EntityPredicate predicate = optional.get();
        return predicate.matches(player.serverLevel(), null, player);
    }

    public static boolean match(Optional<BlockSnapshotPredicate> optional, BlockSnapshot snapshot)
    {
        if(optional.isEmpty())
            return true;
        BlockSnapshotPredicate predicate = optional.get();
        return predicate.test(snapshot);
    }
}
