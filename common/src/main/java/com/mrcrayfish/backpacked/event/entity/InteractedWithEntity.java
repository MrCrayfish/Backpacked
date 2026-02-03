package com.mrcrayfish.backpacked.event.entity;

import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
@FunctionalInterface
public interface InteractedWithEntity extends IFrameworkEvent
{
    void handle(ServerPlayer player, ItemStack stack, Entity entity, List<ResourceLocation> callbacks);

    interface Capture extends IFrameworkEvent
    {
        void handle(ServerPlayer player, ItemStack stack, Entity entity, Consumer<ResourceLocation> idConsumer);
    }
}
