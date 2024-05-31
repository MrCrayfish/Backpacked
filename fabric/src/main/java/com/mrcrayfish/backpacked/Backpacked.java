package com.mrcrayfish.backpacked;

import com.chocohead.mm.api.ClassTinkerers;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mrcrayfish.backpacked.common.WanderingTraderEvents;
import com.mrcrayfish.backpacked.common.backpack.loader.FabricBackpackLoader;
import com.mrcrayfish.framework.FrameworkSetup;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class Backpacked implements ModInitializer
{
    public static final Supplier<EnchantmentCategory> ENCHANTMENT_TYPE = Suppliers.memoize(() -> ClassTinkerers.getEnum(EnchantmentCategory.class, "BACKPACKED$BACKPACK"));

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
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new FabricBackpackLoader());
    }
}
