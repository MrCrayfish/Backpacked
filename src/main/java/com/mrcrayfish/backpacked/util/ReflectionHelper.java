package com.mrcrayfish.backpacked.util;

import net.minecraft.inventory.container.Slot;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Author: MrCrayfish
 */
public class ReflectionHelper
{
    private static final Field SLOT_X = removeFinal(ObfuscationReflectionHelper.findField(Slot.class, "field_75223_e"));
    private static final Field SLOT_Y = removeFinal(ObfuscationReflectionHelper.findField(Slot.class, "field_75221_f"));

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
