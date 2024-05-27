package com.mrcrayfish.backpacked.event.entity;

import com.mrcrayfish.backpacked.common.MovementType;
import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import net.minecraft.server.level.ServerPlayer;

/**
 * Author: MrCrayfish
 */
public interface PlayerTravel extends IFrameworkEvent
{
    void handle(ServerPlayer player, double distance, MovementType type);
}
