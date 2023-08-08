package com.mrcrayfish.backpacked;

import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.impl.event.interaction.InteractionEventsRouter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.InteractionResult;

public class Backpacked implements ModInitializer
{
    private static boolean trinketsLoaded;

    public Backpacked()
    {
        trinketsLoaded = FabricLoader.getInstance().isModLoaded("trinkets");
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
    }

    public static boolean isTrinketsLoaded()
    {
        return trinketsLoaded;
    }
}
