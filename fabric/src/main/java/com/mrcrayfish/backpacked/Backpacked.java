package com.mrcrayfish.backpacked;

import com.chocohead.mm.api.ClassTinkerers;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.framework.FrameworkSetup;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.impl.event.interaction.InteractionEventsRouter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class Backpacked implements ModInitializer
{
    public static final Supplier<EnchantmentCategory> ENCHANTMENT_TYPE = Suppliers.memoize(() -> ClassTinkerers.getEnum(EnchantmentCategory.class, "BACKPACKED$BACKPACK"));

    private static boolean trinketsLoaded;

    public Backpacked()
    {
        FrameworkSetup.run();
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
