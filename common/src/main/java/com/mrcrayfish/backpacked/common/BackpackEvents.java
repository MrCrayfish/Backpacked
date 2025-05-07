package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.BackpackProperties;
import com.mrcrayfish.backpacked.core.ModDataComponents;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.core.ModSyncedDataKeys;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.platform.Services;
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
        PlayerEvents.PICKUP_ITEM.register(BackpackEvents::onPickupItem);
        TickEvents.START_PLAYER.register(BackpackEvents::updateBackpackProperties);
    }

    private static boolean onPickupItem(Player player, ItemEntity entity)
    {
        if(Config.SERVER.backpack.autoEquipOnPickup.get() && player instanceof ServerPlayer serverPlayer)
        {
            ItemStack stack = entity.getItem();
            if(!(stack.getItem() instanceof BackpackItem))
                return false;

            if(Services.BACKPACK.getBackpackStack(serverPlayer).isEmpty())
            {
                if(Services.BACKPACK.setBackpackStack(serverPlayer, stack))
                {
                    ((ServerLevel) entity.level()).getChunkSource().broadcast(entity, new ClientboundTakeItemEntityPacket(entity.getId(), serverPlayer.getId(), stack.getCount()));
                    entity.discard();
                }
                return true;
            }
        }
        return false;
    }

    private static void updateBackpackProperties(Player player)
    {
        if(player.level().isClientSide())
            return;

        Optional<BackpackProperties> cosmeticProperties = ModSyncedDataKeys.COSMETIC_PROPERTIES.getValue(player);
        ItemStack realStack = ModSyncedDataKeys.BACKPACK.getValue(player);
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
