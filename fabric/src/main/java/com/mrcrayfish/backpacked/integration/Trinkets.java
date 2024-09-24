package com.mrcrayfish.backpacked.integration;

import com.mrcrayfish.backpacked.item.BackpackItem;
import com.mrcrayfish.backpacked.item.TrinketBackpackItem;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class Trinkets
{
    /**
     * Gets the ItemStack in the trinket slot the backpack uses. The returned ItemStack may not be a
     * backpack.
     *
     * @param player the player to get the stack from
     * @return An ItemStack from the slot the backpack uses. This may not be a backpack
     */
    public static ItemStack getStackInBackpackSlot(Player player)
    {
        return TrinketsApi.getTrinketComponent(player)
            .flatMap(component -> Optional.ofNullable(component.getInventory().get("chest")))
            .flatMap(map -> Optional.ofNullable(map.get("back")))
            .map(inventory -> inventory.getItem(0))
            .orElse(ItemStack.EMPTY);
    }

    public static ItemStack getBackpackStack(Player player)
    {
        ItemStack stack = TrinketsApi.getTrinketComponent(player)
                .flatMap(component -> Optional.ofNullable(component.getInventory().get("chest")))
                .flatMap(map -> Optional.ofNullable(map.get("back")))
                .map(inventory -> inventory.getItem(0))
                .orElse(ItemStack.EMPTY);
        return stack.getItem() instanceof BackpackItem ? stack : ItemStack.EMPTY;
    }

    public static void setBackpackStack(Player player, ItemStack stack)
    {
        TrinketsApi.getTrinketComponent(player)
            .flatMap(component -> Optional.ofNullable(component.getInventory().get("chest")))
            .flatMap(map -> Optional.ofNullable(map.get("back"))).ifPresent(inventory -> {
                inventory.setItem(0, stack);
            });
    }

    public static <T extends Item & Trinket> void registerTrinket(T trinket)
    {
        TrinketsApi.registerTrinket(trinket, trinket);
    }

    public static BackpackItem createTrinketBackpack(Item.Properties properties)
    {
        TrinketBackpackItem item = new TrinketBackpackItem(properties);
        Trinkets.registerTrinket(item);
        return item;
    }
}
