package com.mrcrayfish.backpacked.client.augment;

import com.mrcrayfish.backpacked.common.augment.Augments;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public final class AugmentHolder<T>
{
    private final Supplier<T> supplier;
    private final Consumer<T> updater;
    private final Augments.Position position;
    private final int backpackIndex;

    public AugmentHolder(Supplier<T> supplier, Consumer<T> updater, Augments.Position position, int backpackIndex)
    {
        this.supplier = supplier;
        this.updater = updater;
        this.position = position;
        this.backpackIndex = backpackIndex;
    }

    public T get()
    {
        return this.supplier.get();
    }

    public void update(T augment)
    {
        this.updater.accept(augment);
    }

    public Augments.Position position()
    {
        return this.position;
    }

    public int backpackIndex()
    {
        return this.backpackIndex;
    }
}
