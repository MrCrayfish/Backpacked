package com.mrcrayfish.backpacked.entity;

import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public interface ILootCapture
{
    @Nullable
    List<ItemEntity> backpacked$GetCapturedDrops();

    void backpacked$StartCapturingDrop();

    void backpacked$EndCapturingDrop();
}
