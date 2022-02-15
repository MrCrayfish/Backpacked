package com.mrcrayfish.backpacked.util;

import net.minecraft.world.inventory.Slot;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Author: MrCrayfish
 */
public class ReflectionHelper
{
    private static final Field SLOT_X = removeFinal(ObfuscationReflectionHelper.findField(Slot.class, "f_40220_"));
    private static final Field SLOT_Y = removeFinal(ObfuscationReflectionHelper.findField(Slot.class, "f_40221_"));

    public static void repositionSlot(Slot slot, int x, int y)
    {
        try
        {
            SLOT_X.set(slot, x);
            SLOT_Y.set(slot, y);
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private static Field removeFinal(Field field)
    {
        try
        {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        }
        catch(NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return field;
    }
}
