package com.mrcrayfish.backpacked.event;

import com.mrcrayfish.backpacked.event.entity.FeedAnimal;
import com.mrcrayfish.framework.api.event.FrameworkEvent;

/**
 * Author: MrCrayfish
 */
public class BackpackedEntityEvents
{
    public static final FrameworkEvent<FeedAnimal> FEED_ANIMAL = new FrameworkEvent<>(listeners -> (animal, player) -> {
        listeners.forEach(listener -> listener.handle(animal, player));
    });
}
