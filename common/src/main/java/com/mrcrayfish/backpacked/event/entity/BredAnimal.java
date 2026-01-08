package com.mrcrayfish.backpacked.event.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;

/**
 * Author: MrCrayfish
 */
@FunctionalInterface
public interface BredAnimal
{
    void handle(Animal first, Animal second, ServerPlayer player);
}
