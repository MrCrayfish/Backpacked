package com.mrcrayfish.backpacked.event.entity;

import com.mrcrayfish.backpacked.common.MovementType;
import net.minecraft.server.level.ServerPlayer;

/**
 * Author: MrCrayfish
 */
public interface PlayerTravel
{
    void handle(ServerPlayer player, double distance, MovementType type);
}
