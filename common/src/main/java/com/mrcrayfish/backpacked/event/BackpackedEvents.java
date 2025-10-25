package com.mrcrayfish.backpacked.event;

import com.mrcrayfish.backpacked.event.block.InteractedWithBlock;
import com.mrcrayfish.backpacked.event.block.MinedBlock;
import com.mrcrayfish.backpacked.event.entity.*;
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

    public static final FrameworkEvent<MinedBlock.CaptureTag> MINED_BLOCK_CAPTURE_TAG = new FrameworkEvent<>(listeners -> player -> {
        for(var listener : listeners) {
            if(listener.handle(player)) {
                return true;
            }
        }
        return false;
    });

    public static final FrameworkEvent<MinedBlock> MINED_BLOCK = new FrameworkEvent<>(listeners -> (snapshot, stack, player) -> {
        listeners.forEach(listener -> listener.handle(snapshot, stack, player));
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

    public static final FrameworkEvent<InteractedWithEntity.Capture> INTERACTED_WITH_ENTITY_CAPTURE = new FrameworkEvent<>(listeners -> (player, stack, entity, consumer) -> {
        listeners.forEach(listener -> listener.handle(player, stack, entity, consumer));
    });

    public static final FrameworkEvent<InteractedWithEntity> INTERACTED_WITH_ENTITY = new FrameworkEvent<>(listeners -> (player, stack, entity, capturedIds) -> {
        listeners.forEach(listener -> listener.handle(player, stack, entity, capturedIds));
    });

    public static final FrameworkEvent<PlayerTravel> PLAYER_TRAVEL = new FrameworkEvent<>(listeners -> (player, distance, style) -> {
        listeners.forEach(listener -> listener.handle(player, distance, style));
    });

    public static final FrameworkEvent<MerchantTrade> MERCHANT_TRADE = new FrameworkEvent<>(listeners -> (merchant, player, stack) -> {
        listeners.forEach(listener -> listener.handle(merchant, player, stack));
    });
}
