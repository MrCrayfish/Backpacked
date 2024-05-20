package com.mrcrayfish.backpacked.event.entity;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;

/**
 * Author: MrCrayfish
 */
@FunctionalInterface
public interface BredAnimal extends IFrameworkEvent
{
    void handle(Animal first, Animal second, ServerPlayer player);
}
