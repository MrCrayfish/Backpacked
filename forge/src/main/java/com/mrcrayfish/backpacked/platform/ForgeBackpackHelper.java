package com.mrcrayfish.backpacked.platform;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.blockentity.ForgeShelfBlockEntity;
import com.mrcrayfish.backpacked.blockentity.ShelfBlockEntity;
import com.mrcrayfish.backpacked.data.pickpocket.PickpocketChallenge;
import com.mrcrayfish.backpacked.data.pickpocket.ForgePickpocketChallenge;
import com.mrcrayfish.backpacked.data.tracker.ForgeUnlockTracker;
import com.mrcrayfish.backpacked.data.tracker.UnlockTracker;
import com.mrcrayfish.backpacked.integration.Curios;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.item.ForgeBackpackItem;
import com.mrcrayfish.backpacked.platform.services.IBackpackHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Author: MrCrayfish
 */
public class ForgeBackpackHelper implements IBackpackHelper
{
    @Override
    @Nullable
    public UnlockTracker getUnlockTracker(Player player, boolean old)
    {
        if(old) player.reviveCaps();
        UnlockTracker tracker = player.getCapability(ForgeUnlockTracker.UNLOCK_TRACKER_CAPABILITY).resolve().orElse(null);
        if(old) player.invalidateCaps();
        return tracker;
    }

    @Override
    public ItemStack getBackpackStack(Player player)
    {
        AtomicReference<ItemStack> backpack = new AtomicReference<>(ItemStack.EMPTY);
        if(Backpacked.isCuriosLoaded())
        {
            backpack.set(Curios.getBackpackStack(player));
        }
        else if(player.getInventory() instanceof ExtendedPlayerInventory inventory)
        {
            ItemStack stack = inventory.getBackpackItems().get(0);
            if(stack.getItem() instanceof BackpackItem)
            {
                backpack.set(stack);
            }
        }
        return backpack.get();
    }

    @Override
    public boolean setBackpackStack(Player player, ItemStack stack)
    {
        if(!(stack.getItem() instanceof BackpackItem) && !stack.isEmpty())
            return false;

        if(Backpacked.isCuriosLoaded())
        {
            Curios.setBackpackStack(player, stack);
            return true;
        }
        else if(player.getInventory() instanceof ExtendedPlayerInventory inventory)
        {
            inventory.getBackpackItems().set(0, stack.copy());
            return true;
        }
        return false;
    }

    @Override
    public EnchantmentCategory getEnchantmentCategory()
    {
        return Backpacked.ENCHANTMENT_TYPE;
    }

    @Override
    public boolean isUsingThirdPartySlot()
    {
        return Backpacked.isCuriosLoaded();
    }

    @Override
    public boolean isBackpackVisible(Player player)
    {
        if(Backpacked.isCuriosLoaded())
        {
            return Curios.isBackpackVisible(player);
        }
        return true;
    }

    @Override
    public PickpocketChallenge getPickpocketChallenge(Entity entity)
    {
        return entity.getCapability(ForgePickpocketChallenge.PICKPOCKET_CAPABILITY).resolve().orElse(null);
    }

    @Override
    public ShelfBlockEntity createShelfBlockEntityType(BlockPos pos, BlockState state)
    {
        return new ForgeShelfBlockEntity(pos, state);
    }

    @Override
    public void openBackpackScreen(ServerPlayer openingPlayer, Container inventory, int cols, int rows, boolean owner, Component title)
    {
        NetworkHooks.openScreen(openingPlayer, new SimpleMenuProvider((id, playerInventory, entity) -> {
            return new BackpackContainerMenu(id, openingPlayer.getInventory(), inventory, cols, rows, owner);
        }, title), buffer -> {
            buffer.writeVarInt(cols);
            buffer.writeVarInt(rows);
            buffer.writeBoolean(owner);
        });
    }

    @Override
    public BackpackItem createBackpackItem(Item.Properties properties)
    {
        return new ForgeBackpackItem(properties);
    }
}
