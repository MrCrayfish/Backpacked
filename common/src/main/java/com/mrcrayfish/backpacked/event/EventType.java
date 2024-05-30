package com.mrcrayfish.backpacked.event;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.event.block.MinedBlock;
import com.mrcrayfish.backpacked.event.entity.FeedAnimal;
import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import com.mrcrayfish.framework.event.IEntityEvent;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public record EventType<T extends IFrameworkEvent>(ResourceLocation id)
{
    public static final EventType<IEntityEvent.LivingEntityDeath> LIVING_ENTITY_DEATH = new EventType<>(new ResourceLocation("framework", "entity_death"));
    public static final EventType<MinedBlock> MINED_BLOCK = new EventType<>(new ResourceLocation(Constants.MOD_ID, "mined_block"));
    public static final EventType<FeedAnimal> FEED_ANIMAL = new EventType<>(new ResourceLocation(Constants.MOD_ID, "feed_animal"));
}
