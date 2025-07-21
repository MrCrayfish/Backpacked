package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.BackpackProperties;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;


public class BackpackEvents
{
    public static void init()
    {
        TickEvents.START_PLAYER.register(BackpackEvents::updateBackpackProperties);
    }

    private static void updateBackpackProperties(Player player)
    {
        if(player.level().isClientSide())
            return;

        Optional<BackpackProperties> cosmeticProperties = ModSyncedDataKeys.COSMETIC_PROPERTIES.getValue(player);
        ItemStack realStack = BackpackHelper.getFirstBackpackStack(player);
        if(realStack.is(ModItems.BACKPACK.get()))
        {
            BackpackProperties realProperties = realStack.get(ModDataComponents.BACKPACK_PROPERTIES.get());
            if(realProperties == null)
            {
                throw new RuntimeException("BackpackProperties is null. This should not happen!");
            }
            if(cosmeticProperties.isEmpty() || !realProperties.equals(cosmeticProperties.get()))
            {
                ModSyncedDataKeys.COSMETIC_PROPERTIES.setValue(player, Optional.of(realProperties));
            }
        }
        else if(cosmeticProperties.isPresent())
        {
            ModSyncedDataKeys.COSMETIC_PROPERTIES.setValue(player, Optional.empty());
        }
    }
}
