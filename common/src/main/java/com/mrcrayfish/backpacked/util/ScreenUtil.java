package com.mrcrayfish.backpacked.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public class ScreenUtil
{
    public static boolean isPointInArea(int px, int py, int x, int y, int width, int height)
    {
        return px >= x && px < x + width && py >= y && py < y + height;
    }

    @Nullable
    public static Tooltip createMultilineTooltip(Component... components)
    {
        if(components.length == 0)
            return null;

        MutableComponent lines = Component.literal("");
        lines = lines.append(components[0]);
        for(int i = 1; i < components.length; i++)
        {
            lines.append("\n");
            lines = lines.append(components[i]);
        }
        return Tooltip.create(lines);
    }
}
