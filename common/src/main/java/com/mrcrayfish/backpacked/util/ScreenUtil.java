package com.mrcrayfish.backpacked.util;

import com.mrcrayfish.backpacked.client.Icons;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class ScreenUtil
{
    public static final ResourceLocation ICON_FONT = Utils.rl("icons");

    public static void scissor(int x, int y, int width, int height)
    {
        Minecraft mc = Minecraft.getInstance();
        int scale = (int) mc.getWindow().getGuiScale();
        GL11.glScissor(x * scale, mc.getWindow().getScreenHeight() - y * scale - height * scale, Math.max(0, width * scale), Math.max(0, height * scale));
    }

    public static boolean isPointInArea(int px, int py, int x, int y, int width, int height)
    {
        return px >= x && px < x + width && py >= y && py < y + height;
    }

    public static boolean isPointInArea(ScreenRectangle rect, int px, int py)
    {
        return px >= rect.left() && px < rect.right() && py >= rect.top() && py < rect.bottom();
    }

    @Nullable
    public static Tooltip createMultilineTooltip(List<Component> components)
    {
        if(components.isEmpty())
            return null;

        MutableComponent lines = Component.literal("");
        lines = lines.append(components.get(0));
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
