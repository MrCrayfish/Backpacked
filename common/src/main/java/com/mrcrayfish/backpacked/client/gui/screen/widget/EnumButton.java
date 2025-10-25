package com.mrcrayfish.backpacked.client.gui.screen.widget;

import com.mrcrayfish.backpacked.client.SpriteProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;

import java.util.List;
import java.util.function.BiConsumer;

public class EnumButton<T extends Enum<T> & SpriteProvider> extends Button
{
    private final List<T> values;
    private final BiConsumer<EnumButton<T>, T> callback;
    private T value;

    public EnumButton(int x, int y, int width, int height, T initialValue, BiConsumer<EnumButton<T>, T> callback)
    {
        super(x, y, width, height, CommonComponents.EMPTY, button -> ((EnumButton<?>) button).nextValue(), DEFAULT_NARRATION);
        this.values = List.of(initialValue.getDeclaringClass().getEnumConstants());
        this.value = initialValue;
        this.callback = callback;
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
        graphics.blitSprite(this.value.getSprite(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.width, this.height);
        if(this.isHovered && this.active)
        {
            graphics.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -2130706433, -2130706433);
        }
    }
}
