package com.mrcrayfish.backpacked.util;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectedMethod<C, R>
{
    private final Method method;

    public ReflectedMethod(Class<C> targetClass, String method, Class<?> ... parameters)
    {
        try
        {
            this.method = targetClass.getDeclaredMethod(method, parameters);
            this.method.setAccessible(true);
        }
        catch(NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public R invoke(@Nullable C obj, Object ... arguments)
    {
        try
        {
            return (R) this.method.invoke(obj, arguments);
        }
        catch(InvocationTargetException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}