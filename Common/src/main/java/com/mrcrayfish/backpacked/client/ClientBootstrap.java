package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.client.model.backpack.BackpackModel;
import com.mrcrayfish.backpacked.client.renderer.entity.layers.BackpackLayer;
import com.mrcrayfish.backpacked.common.backpack.BackpackManager;
import com.mrcrayfish.framework.api.event.InputEvents;

/**
 * Author: MrCrayfish
 */
public class ClientBootstrap
{
    public static void init()
    {
        BackpackManager.instance().getRegisteredBackpacks().forEach(backpack -> {
            BackpackLayer.registerModel(backpack.getId(), () -> (BackpackModel) backpack.getModelSupplier().get());
        });
        InputEvents.REGISTER_KEY_MAPPING.register(consumer -> consumer.accept(Keys.KEY_BACKPACK));
        ClientEvents.init();
    }
}
