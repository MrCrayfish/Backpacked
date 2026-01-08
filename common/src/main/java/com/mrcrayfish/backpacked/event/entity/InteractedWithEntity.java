package com.mrcrayfish.backpacked.event.entity;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
@FunctionalInterface
public interface InteractedWithEntity
{
    void handle(ServerPlayer player, ItemStack stack, Entity entity, List<Identifier> callbacks);

    interface Capture
    {
        void handle(ServerPlayer player, ItemStack stack, Entity entity, Consumer<Identifier> idConsumer);
    }
}
