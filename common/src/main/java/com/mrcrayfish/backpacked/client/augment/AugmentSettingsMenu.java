package com.mrcrayfish.backpacked.client.augment;

import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class AugmentSettingsMenu
{
    private static final Map<AugmentType<?>, BiFunction<PopupMenuHandler, ? extends Augment<?>, PopupMenu>> FACTORIES = new HashMap<>();

    public static <T extends Augment<T>> void registerFactory(AugmentType<T> type, BiFunction<PopupMenuHandler, T, PopupMenu> menu)
    {
        if(FACTORIES.put(type, menu) != null)
        {
            throw new IllegalStateException("Duplicate factory for augment type: " + type.id());
        }
    }

    public static <T extends Augment<T>> boolean hasFactory(AugmentType<T> type)
    {
        return FACTORIES.containsKey(type);
    }

    @SuppressWarnings("unchecked")
    public static BiFunction<PopupMenuHandler, Augment<?>, PopupMenu> getFactory(Augment<?> augment)
    {
        return (BiFunction<PopupMenuHandler, Augment<?>, PopupMenu>) FACTORIES.get(augment.type());
    }
}
