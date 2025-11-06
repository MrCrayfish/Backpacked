package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.platform.Services;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Map;

/**
 * A special class for processing and holding the sort index of items in creative tabs. This is used
 * in {@link ItemSorting#CREATIVE_CATEGORY} to sort items in the backpack based on the creative tab.
 * However, some preprocessing of the creative tabs needs to be applied to not only determine the
 * creative tab the item is placed in, but also the index of the item in the creative tab itself.
 * The resulting sort index is simply given by outputting ItemStacks for each creative tab using
 * {@link net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator} and getting then incrementing
 * a counter for each ItemStack. Each creative tab is processed in the same order it was registered.
 */
public final class CreativeCategorySort
{
    private static final Map<Item, Integer> CREATIVE_SORT_INDEX_MAP = new Object2IntOpenHashMap<>();
    private static boolean needsSorting = true; // TODO might need to be invalidated

    /**
     * Gets the sort index of the given item or {@link Integer#MAX_VALUE} if it doesn't exist (meaning it will be last)
     *
     * @param item the item to get the sort index for
     * @return an integer representing the sort index
     */
    public static int getSortIndex(Item item)
    {
        return CREATIVE_SORT_INDEX_MAP.getOrDefault(item, Integer.MAX_VALUE);
    }

    /**
     * Builds the sort index map, otherwise ignored if already built
     *
     * @param access a RegistryAccess instance
     */
    public static void buildSortIndex(RegistryAccess access)
    {
        if(!needsSorting)
            return;
        MutableInt sortIndex = new MutableInt(0);
        BuiltInRegistries.CREATIVE_MODE_TAB.stream()
            .filter(tab -> tab.getType() == CreativeModeTab.Type.CATEGORY)
            .forEach(tab -> {
                var generator = ((CreativeModeTabAccess) tab).backpacked$displayItemsGenerator();
                var params = new CreativeModeTab.ItemDisplayParameters(FeatureFlags.DEFAULT_FLAGS, false, access);
                var output = Services.PLATFORM.createCreativeTabOutput(stack -> {
                    CREATIVE_SORT_INDEX_MAP.computeIfAbsent(stack.getItem(), item -> sortIndex.getAndIncrement());
                });
                generator.accept(params, output);
            });
        needsSorting = false;
    }
}
