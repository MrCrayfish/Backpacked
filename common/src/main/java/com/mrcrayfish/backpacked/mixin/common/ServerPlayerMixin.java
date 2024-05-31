package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.event.BackpackedInteractAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements BackpackedInteractAccess
{
    @Unique
    public List<ResourceLocation> backpacked$CapturedInteractIds = new ArrayList<>();

    @Override
    public List<ResourceLocation> getBackpacked$CapturedInteractIds()
    {
        return this.backpacked$CapturedInteractIds;
    }
}
