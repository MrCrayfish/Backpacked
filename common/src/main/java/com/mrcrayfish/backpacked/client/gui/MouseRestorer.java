package com.mrcrayfish.backpacked.client.gui;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.lwjgl.glfw.GLFW;

/**
 * A simple utility that restores the position of the mouse when transferring between
 * different container GUI. By default, Minecraft resets the position of the mouse to the
 * center of the screen after a container screen is closed, which is not ideal when transferring.
 */
public class MouseRestorer
{
    private static boolean captured;
    private static double captureTime;
    private static double capturedX;
    private static double capturedY;

    /**
     * Capture the current position of the mouse
     */
    public static void capturePosition()
    {
        MouseHandler handler = Minecraft.getInstance().mouseHandler;
        captureTime = Util.getMillis();
        capturedX = handler.xpos();
        capturedY = handler.ypos();
        captured = true;
    }

    /**
     * Restores the position of the mouse, only if something was captured and
     * if the capture is within a specific time frame.
     */
    public static void loadCapturedPosition()
    {
        if(captured && Util.getMillis() - captureTime < 100)
        {
            Window window = Minecraft.getInstance().getWindow();
            GLFW.glfwSetCursorPos(window.getWindow(), capturedX, capturedY);
        }
        captured = false;
    }
}
