package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.LabelAndDescription;
import com.mrcrayfish.framework.api.client.screen.widget.Buttons;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BackpackButtons
{
    public static final WidgetSprites DEFAULT_SPRITES = new WidgetSprites(
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_disabled"),
        ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/button_enabled_focused")
    );

    public static FrameworkButton.Builder builder()
    {
        return FrameworkButton.builder().setTexture(DEFAULT_SPRITES);
    }

    public static FrameworkButton.Builder onOff(Supplier<Boolean> getter, Consumer<Boolean> setter)
    {
        return onOff(getter, setter, newValue -> {});
    }

    public static FrameworkButton.Builder onOff(Supplier<Boolean> getter, Consumer<Boolean> setter, Consumer<Boolean> onChanged)
    {
        return Buttons.createOnOff(getter, setter, onChanged).setTexture(DEFAULT_SPRITES);
    }

    public static FrameworkButton.Builder toggle(Supplier<Boolean> getter, Consumer<Boolean> setter, Consumer<Boolean> onChanged)
    {
        return Buttons.createToggle(getter, setter, onChanged).setTexture(DEFAULT_SPRITES);
    }

    public static FrameworkButton.Builder state(Supplier<Boolean> getter, Consumer<Boolean> setter, Consumer<Boolean> onChanged)
    {
        return FrameworkButton.builder().setAction(btn -> {
            boolean newValue = !getter.get();
            setter.accept(newValue);
            onChanged.accept(newValue);
        });
    }

    public static <T extends Enum<T> & LabelAndDescription>FrameworkButton.Builder values(Supplier<T> getter, Consumer<T> setter, Consumer<T> onChanged)
    {
        return Buttons.createValues(LabelAndDescription::label, LabelAndDescription::description, getter, setter, onChanged).setTexture(DEFAULT_SPRITES);
    }
}
