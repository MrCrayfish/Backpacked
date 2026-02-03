package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.framework.api.client.screen.widget.texture.FrameworkTexture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class EnumButton<T extends Enum<T>> extends Button
{
    private final List<T> values;
    private final BiConsumer<EnumButton<T>, T> callback;
    private final Function<T, FrameworkTexture> icon;
    private T value;

    public EnumButton(int x, int y, int width, int height, T initialValue, BiConsumer<EnumButton<T>, T> callback, Function<T, FrameworkTexture> icon)
    {
        super(x, y, width, height, CommonComponents.EMPTY, button -> ((EnumButton<?>) button).nextValue(), DEFAULT_NARRATION);
        this.values = List.of(initialValue.getDeclaringClass().getEnumConstants());
        this.value = initialValue;
        this.callback = callback;
        this.icon = icon;
    }

    private void nextValue()
    {
        int nextIndex = (this.value.ordinal() + 1) % this.values.size();
        this.value = this.values.get(nextIndex);
        this.callback.accept(this, this.value);
    }

    public void set(T value)
    {
        this.value = value;
        this.callback.accept(this, this.value);
    }

    public T getValue()
    {
        return this.value;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        this.icon.apply(this.value).draw(graphics, this.getX(), this.getY(), this.width, this.height);
        if(this.isHovered && this.active)
        {
            graphics.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -2130706433, -2130706433);
        }
    }
}
