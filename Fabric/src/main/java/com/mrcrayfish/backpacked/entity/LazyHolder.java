package com.mrcrayfish.backpacked.entity;

import com.mrcrayfish.backpacked.util.Serializable;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class LazyHolder<T extends Serializable>
{
    private final CompoundTag data;
    private final Supplier<T> instanceSupplier;
    private T instance;

    public LazyHolder(CompoundTag data, Supplier<T> instanceSupplier)
    {
        this.data = data;
        this.instanceSupplier = instanceSupplier;
    }

    public T get()
    {
        if(this.instance == null)
        {
            this.instance = this.create();
        }
        return this.instance;
    }

    private T create()
    {
        T t = this.instanceSupplier.get();
        t.deserialize(this.data);
        return t;
    }

    public CompoundTag serialize()
    {
        if(this.instance != null)
        {
            return this.instance.serialize();
        }
        return this.data;
    }
}
