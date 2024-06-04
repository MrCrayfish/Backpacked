package com.mrcrayfish.backpacked.integration;

import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Author: MrCrayfish
 */
public class Curios
{
    public static final String SLOT_IDENTIFIER = "back";

    public static ItemStack getBackpackStack(Player player)
    {
        AtomicReference<ItemStack> backpack = new AtomicReference<>(ItemStack.EMPTY);
        Optional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(player);
        optional.ifPresent(itemHandler ->
        {
            Optional<ICurioStacksHandler> stacksOptional = itemHandler.getStacksHandler(SLOT_IDENTIFIER);
            stacksOptional.ifPresent(stacksHandler ->
            {
                ItemStack stack = stacksHandler.getStacks().getStackInSlot(0);
                if(stack.getItem() instanceof BackpackItem)
                {
                    backpack.set(stack);
                }
            });
        });
        return backpack.get();
    }

    public static void setBackpackStack(Player player, ItemStack stack)
    {
        Optional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(player);
        optional.ifPresent(itemHandler ->
        {
            Optional<ICurioStacksHandler> stacksOptional = itemHandler.getStacksHandler(SLOT_IDENTIFIER);
            stacksOptional.ifPresent(stacksHandler ->
            {
                stacksHandler.getStacks().setStackInSlot(0, stack.copy());
            });
        });
    }

    public static boolean isBackpackVisible(Player player)
    {
        AtomicReference<Boolean> visible = new AtomicReference<>(true);
        Optional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(player);
        optional.ifPresent(itemHandler -> {
            Optional<ICurioStacksHandler> stacksOptional = itemHandler.getStacksHandler(SLOT_IDENTIFIER);
            stacksOptional.ifPresent(stacksHandler -> {
                visible.set(stacksHandler.getRenders().get(0));
            });
        });
        return visible.get();
    }
}
