package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.backpack.loader.FabricBackpackLoader;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.integration.BackpackAccessory;
import com.mrcrayfish.framework.FrameworkSetup;
import io.wispforest.accessories.api.AccessoriesAPI;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionResult;

public class Backpacked implements ModInitializer
{
    public Backpacked()
    {
        FrameworkSetup.run();
    }

    @Override
    public void onInitialize()
    {
        Bootstrap.init();
        UseEntityCallback.EVENT.register((player, level, hand, entity, result) ->
        {
            if(!level.isClientSide() && WanderingTraderEvents.onInteract(entity, player))
            {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });
        AccessoriesAPI.registerAccessory(ModItems.BACKPACK.get(), new BackpackAccessory());
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new FabricBackpackLoader());
    }
}
