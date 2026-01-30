package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.CosmeticProperties;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
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
        TickEvents.START_PLAYER.register(player -> {
            BackpackEvents.updateBackpackProperties(player);
            BackpackEvents.applyImmortalCooldown(player);
            BackpackEvents.updateBackpackScale(player);
        });
    }

    private static void updateBackpackProperties(Player player)
    {
        if(player.level().isClientSide())
            return;

        Optional<CosmeticProperties> cosmeticProperties = ModSyncedDataKeys.COSMETIC_PROPERTIES.getValue(player);
        ItemStack realStack = BackpackHelper.getFirstBackpackStack(player);
        if(realStack.is(ModItems.BACKPACK.get()))
        {
            // TODO should be fine but look into performance since this is every tick
            CosmeticProperties realProperties = CosmeticProperties.view(realStack, CosmeticProperties.DEFAULT);
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

    private static void applyImmortalCooldown(Player player)
    {
        if(player.level().isClientSide())
            return;

        int cooldown = ModSyncedDataKeys.IMMORTAL_COOLDOWN.getValue(player);
        if(cooldown > 0)
        {
            ModSyncedDataKeys.IMMORTAL_COOLDOWN.setValue(player, cooldown - 1);
        }
    }

    private static void updateBackpackScale(Player player)
    {
        if(player.level().isClientSide())
            return;

        int scale = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.GIANT.get()).size();
        ModSyncedDataKeys.BACKPACK_SCALE.setValue(player, scale);
    }
}
