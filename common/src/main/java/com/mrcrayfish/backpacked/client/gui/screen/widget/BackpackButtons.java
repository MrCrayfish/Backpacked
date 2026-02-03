package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.backpacked.client.LabelAndDescription;
import com.mrcrayfish.backpacked.client.TextureDefinitions;
import com.mrcrayfish.framework.api.client.screen.widget.Buttons;
import com.mrcrayfish.framework.api.client.screen.widget.FrameworkButton;
import com.mrcrayfish.framework.api.client.screen.widget.texture.WidgetTextures;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BackpackButtons
{
    public static final WidgetTextures DEFAULT_SPRITES = new WidgetTextures(
        TextureDefinitions.BUTTON_ENABLED,
        TextureDefinitions.BUTTON_DISABLED,
        TextureDefinitions.BUTTON_ENABLED_HOVERED
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
