package com.mrcrayfish.backpacked.client.gui.screen.widget;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.function.Consumer;

public final class Action<T>
{
    private final Consumer<T> handler;
    private final Holder<SoundEvent> sound;

    private Action(Consumer<T> handler, Holder<SoundEvent> sound)
    {
        this.handler = handler;
        this.sound = sound;
    }

    public Consumer<T> handler()
    {
        return this.handler;
    }

    public Holder<SoundEvent> sound()
    {
        return this.sound;
    }

    public static <T> Action<T> create(Consumer<T> action)
    {
        return new Action<>(action, SoundEvents.UI_BUTTON_CLICK);
    }

    public static <T> Action<T> create(Consumer<T> action, Holder<SoundEvent> sound)
    {
        return new Action<>(action, sound);
    }
}
