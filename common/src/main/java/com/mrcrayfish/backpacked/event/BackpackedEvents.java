package com.mrcrayfish.backpacked.event;

import com.mrcrayfish.backpacked.event.block.MinedBlock;
import com.mrcrayfish.backpacked.event.block.InteractedWithBlock;
import com.mrcrayfish.backpacked.event.entity.BredAnimal;
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

    public static final FrameworkEvent<BredAnimal> BRED_ANIMAL = new FrameworkEvent<>(listeners -> (first, second, player) -> {
        listeners.forEach(listener -> listener.handle(first, second, player));
    });

    public static final FrameworkEvent<MinedBlock.CaptureTag> MINED_BLOCK_CAPTURE_TAG = new FrameworkEvent<>(listeners -> (state, stack, player) -> {
        for(var listener : listeners) {
            if(listener.handle(state, stack, player)) {
                return true;
            }
        }
        return false;
    });

    public static final FrameworkEvent<MinedBlock> MINED_BLOCK = new FrameworkEvent<>(listeners -> (state, stack, tag, player) -> {
        listeners.forEach(listener -> listener.handle(state, stack, tag, player));
    });

    public static final FrameworkEvent<ExploreUpdate> EXPLORE_UPDATE = new FrameworkEvent<>(listeners -> (key, player) -> {
        listeners.forEach(listener -> listener.handle(key, player));
    });

    public static final FrameworkEvent<InteractedWithBlock.CaptureTag> INTERACTED_WITH_BLOCK_CAPTURE_TAG = new FrameworkEvent<>(listeners -> (state, stack, player) -> {
        for(var listener : listeners) {
            if(listener.handle(state, stack, player)) {
                return true;
            }
        }
        return false;
    });

    public static final FrameworkEvent<InteractedWithBlock> INTERACTED_WITH_BLOCK = new FrameworkEvent<>(listeners -> (state, stack, tag, player) -> {
        listeners.forEach(listener -> listener.handle(state, stack, tag, player));
    });
}
