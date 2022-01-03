package com.mrcrayfish.backpacked.item;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.integration.Curios;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.inventory.BackpackedInventoryAccess;
import com.mrcrayfish.backpacked.inventory.ExtendedPlayerInventory;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BackpackItem extends Item
{
    public static final TranslatableComponent BACKPACK_TRANSLATION = new TranslatableComponent("container.backpack");

    public BackpackItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn)
    {
        ItemStack heldItem = playerIn.getItemInHand(handIn);
        if(playerIn.getInventory() instanceof ExtendedPlayerInventory inventory)
        {
            if(inventory.getBackpackItems().get(0).isEmpty())
            {
                inventory.setItem(41, heldItem.copy());
                heldItem.setCount(0);
                playerIn.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
            }
        }
        return new InteractionResultHolder<>(InteractionResult.FAIL, heldItem);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        if(!Backpacked.isCuriosLoaded())
        {
            return null;
        }
        return Curios.createBackpackProvider(stack);
    }

    public void showInventory(ServerPlayer player)
    {
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(!backpack.isEmpty())
        {
            BackpackInventory backpackInventory = ((BackpackedInventoryAccess) player).getBackpackedInventory();
            if(backpackInventory == null)
                return;
            Component title = backpack.hasCustomHoverName() ? backpack.getHoverName() : BACKPACK_TRANSLATION;
            int rows = this.getRowCount();
            NetworkHooks.openGui(player, new SimpleMenuProvider((id, playerInventory, entity) -> {
                return new BackpackContainerMenu(id, player.getInventory(), backpackInventory, rows);
            }, title), buffer -> buffer.writeVarInt(rows));
        }
    }

    public int getRowCount()
    {
        return Config.COMMON.backpackInventorySize.get();
    }

    @OnlyIn(Dist.CLIENT)
    public BackpackModel getDefaultModel()
    {
        return ClientHandler.getModelInstances().getStandardModel();
    }
}
