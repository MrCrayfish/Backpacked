package com.mrcrayfish.backpacked.common.challenge;

import com.mrcrayfish.backpacked.common.BlockSnapshot;
import com.mrcrayfish.backpacked.platform.Services;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class PredicateUtils
{
    public static boolean testPredicate(Optional<BlockPredicate> optional, BlockSnapshot snapshot)
    {
        return Services.PLATFORM.testPredicate(optional, snapshot.state(), snapshot.tag());
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
