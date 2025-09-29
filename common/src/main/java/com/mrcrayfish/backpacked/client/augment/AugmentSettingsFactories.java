package com.mrcrayfish.backpacked.client.augment;

import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenu;
import com.mrcrayfish.backpacked.client.gui.screen.widget.popup.PopupMenuHandler;
import com.mrcrayfish.backpacked.common.augment.Augment;
import com.mrcrayfish.backpacked.common.augment.AugmentType;
import org.apache.commons.lang3.function.TriFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AugmentSettingsFactories
{
    private static final Map<AugmentType<?>, TriFunction<?, ?, ?, ?>> FACTORIES = new HashMap<>();

    public static <T extends Augment<T>> void registerFactory(AugmentType<T> type, TriFunction<PopupMenuHandler, T, Consumer<T>, PopupMenu> menu)
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
    public static TriFunction<PopupMenuHandler, Augment<?>, Consumer<Augment<?>>, PopupMenu> getFactory(Augment<?> augment)
    {
        return (TriFunction<PopupMenuHandler, Augment<?>, Consumer<Augment<?>>, PopupMenu>) FACTORIES.get(augment.type());
    }
}
