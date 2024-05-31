package com.mrcrayfish.backpacked.common.challenge;

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

    public static boolean testPredicate(Optional<EntityPredicate> optional, ServerPlayer player, Entity entity)
    {
        if(optional.isEmpty())
            return true;
        EntityPredicate predicate = optional.get();
        return predicate.matches(player, entity);
    }
}
