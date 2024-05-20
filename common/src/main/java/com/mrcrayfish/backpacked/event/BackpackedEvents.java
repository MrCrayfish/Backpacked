package com.mrcrayfish.backpacked.event;

import com.mrcrayfish.backpacked.event.block.MinedBlock;
import com.mrcrayfish.backpacked.event.entity.ExploreUpdate;
import com.mrcrayfish.backpacked.event.entity.FeedAnimal;
import com.mrcrayfish.framework.api.event.FrameworkEvent;

/**
 * Author: MrCrayfish
 */
public class BackpackedEvents
{
    public static final FrameworkEvent<FeedAnimal> FEED_ANIMAL = new FrameworkEvent<>(listeners -> (animal, player) -> {
        listeners.forEach(listener -> listener.handle(animal, player));
    });

    public static final FrameworkEvent<MinedBlock> MINED_BLOCK = new FrameworkEvent<>(listeners -> (state, player) -> {
        listeners.forEach(listener -> listener.handle(state, player));
    });

    public static final FrameworkEvent<ExploreUpdate> EXPLORE_UPDATE = new FrameworkEvent<>(listeners -> (key, player) -> {
        listeners.forEach(listener -> listener.handle(key, player));
    });
}
