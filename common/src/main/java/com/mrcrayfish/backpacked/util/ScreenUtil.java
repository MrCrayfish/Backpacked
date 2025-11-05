package com.mrcrayfish.backpacked.util;

import com.mrcrayfish.backpacked.client.Icons;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ScreenUtil
{
    public static final ResourceLocation ICON_FONT = Utils.rl("icons");

    public static boolean isPointInArea(int px, int py, int x, int y, int width, int height)
    {
        return px >= x && px < x + width && py >= y && py < y + height;
    }

    @Nullable
    public static Tooltip createMultilineTooltip(List<Component> components)
    {
        if(components.isEmpty())
            return null;

        MutableComponent lines = Component.literal("");
        lines = lines.append(components.getFirst());
        for(int i = 1; i < components.size(); i++)
        {
            lines.append("\n");
            lines = lines.append(components.get(i));
        }
        return Tooltip.create(lines);
    }

    public static Component join(String delimiter, Component ... components)
    {
        if(components.length == 0)
            return CommonComponents.EMPTY;

        MutableComponent builder = Component.literal("");
        builder = builder.append(components[0]);
        for(int i = 1; i < components.length; i++)
        {
            builder = builder.append(delimiter);
            builder = builder.append(components[i]);
        }
        return builder;
    }

    public static MutableComponent getIconComponent(Icons icon)
    {
        MutableComponent component = Component.literal(String.valueOf((char) (33 + icon.ordinal())));
        component.setStyle(component.getStyle().withColor(ChatFormatting.WHITE).withFont(ICON_FONT));
        return component;
    }

    public static Component getShiftIcon()
    {
        MutableComponent component = Component.literal(String.valueOf(new char[]{
            (char) (33 + Icons.SHIFT_1.ordinal()),
            (char) (33 + Icons.SHIFT_2.ordinal())
        }));
        component.setStyle(component.getStyle().withColor(ChatFormatting.WHITE).withFont(ICON_FONT));
        return component;
    }
}
