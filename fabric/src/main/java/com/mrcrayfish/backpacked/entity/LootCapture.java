package com.mrcrayfish.backpacked.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public interface LootCapture
{
    void backpacked$StartCapturingDrop(ServerPlayer player);

    void backpacked$EndCapturingDrop();
}
