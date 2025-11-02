package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.common.augment.AugmentHandler;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class AugmentEvents
{
    @SubscribeEvent(priority = EventPriority.LOWEST) // Target low to give all other mods
    private static void onPreItemPickup(ItemEntityPickupEvent.Pre event)
    {
        if(event.canPickup().isFalse())
            return;

        if(AugmentHandler.beforeItemPickup(event.getPlayer(), event.getItemEntity(), event.getItemEntity().getTarget()))
            event.setCanPickup(TriState.FALSE);
    }
}
