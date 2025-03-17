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
import net.minecraft.core.component.DataComponents;
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

        ItemStack cosmeticStack = ModSyncedDataKeys.COSMETIC_BACKPACK.getValue(player);
        ItemStack realStack = ModSyncedDataKeys.BACKPACK.getValue(player);
        if(!realStack.isEmpty())
        {
            if(cosmeticStack.isEmpty() || !cosmeticStack.is(ModItems.BACKPACK.get()))
            {
                ItemStack copy = realStack.copy();
                copy.remove(DataComponents.CONTAINER);
                ModSyncedDataKeys.COSMETIC_BACKPACK.setValue(player, copy);
                return;
            }
            BackpackProperties realProperties = realStack.getOrDefault(ModDataComponents.BACKPACK_PROPERTIES.get(), BackpackProperties.DEFAULT);
            BackpackProperties cosmeticProperties = cosmeticStack.get(ModDataComponents.BACKPACK_PROPERTIES.get());
            if(!realProperties.equals(cosmeticProperties))
            {
                ItemStack copy = cosmeticStack.copy();
                copy.set(ModDataComponents.BACKPACK_PROPERTIES.get(), realProperties);
                ModSyncedDataKeys.COSMETIC_BACKPACK.setValue(player, copy);
            }
        }
        else if(!cosmeticStack.isEmpty())
        {
            ModSyncedDataKeys.COSMETIC_BACKPACK.setValue(player, ItemStack.EMPTY);
        }
    }
}
