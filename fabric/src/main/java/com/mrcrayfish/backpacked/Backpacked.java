package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.backpack.loader.FabricBackpackLoader;
import com.mrcrayfish.backpacked.core.ModPointOfInterests;
import com.mrcrayfish.backpacked.integration.YoureInGraveDangerSupport;
import com.mrcrayfish.backpacked.mixin.PoiTypesAccessor;
import com.mrcrayfish.framework.FrameworkSetup;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.village.poi.PoiType;

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
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(FabricBackpackLoader.ID, FabricBackpackLoader::new);

        // Register shelf poi type and populate the poi state map
        Holder<PoiType> holder = Registry.registerForHolder(BuiltInRegistries.POINT_OF_INTEREST_TYPE, ModPointOfInterests.BACKPACK_SHELF.key(), ModPointOfInterests.BACKPACK_SHELF.value());
        holder.value().matchingStates().forEach(state -> PoiTypesAccessor.getPoiMap().put(state, holder));

        if(FabricLoader.getInstance().isModLoaded("yigd"))
        {
            YoureInGraveDangerSupport.init();
        }
    }
}
