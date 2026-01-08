package com.mrcrayfish.backpacked.event.entity;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface FeedAnimal
{
    void handle(Animal animal, Player player);
}
