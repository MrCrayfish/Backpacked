package com.mrcrayfish.backpacked.event;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.event.block.MinedBlock;
import com.mrcrayfish.backpacked.event.entity.BredAnimal;
import com.mrcrayfish.backpacked.event.entity.ExploreUpdate;
import com.mrcrayfish.backpacked.event.entity.FeedAnimal;
import com.mrcrayfish.framework.api.event.IFrameworkEvent;
import com.mrcrayfish.framework.event.IEntityEvent;
import com.mrcrayfish.framework.event.IPlayerEvent;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public record EventType<T extends IFrameworkEvent>(ResourceLocation id)
{
    public static final EventType<IEntityEvent.LivingEntityDeath> LIVING_ENTITY_DEATH = new EventType<>(new ResourceLocation("framework", "entity_death"));
    public static final EventType<IPlayerEvent.CraftItem> CRAFTED_ITEM = new EventType<>(new ResourceLocation("framework", "crafted_item"));
    public static final EventType<MinedBlock> MINED_BLOCK = new EventType<>(new ResourceLocation(Constants.MOD_ID, "mined_block"));
    public static final EventType<FeedAnimal> FEED_ANIMAL = new EventType<>(new ResourceLocation(Constants.MOD_ID, "feed_animal"));
    public static final EventType<BredAnimal> BRED_ANIMAL = new EventType<>(new ResourceLocation(Constants.MOD_ID, "bred_animal"));
    public static final EventType<ExploreUpdate> EXPLORE_UPDATE = new EventType<>(new ResourceLocation(Constants.MOD_ID, "explore_update"));
}
