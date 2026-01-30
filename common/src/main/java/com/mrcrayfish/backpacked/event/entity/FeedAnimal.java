package com.mrcrayfish.backpacked.event.entity;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface FeedAnimal extends IFrameworkEvent // TODO DONE
{
    void handle(Animal animal, Player player);
}
