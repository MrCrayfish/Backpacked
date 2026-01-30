package com.mrcrayfish.backpacked.client.gui;

import com.mrcrayfish.backpacked.platform.ClientServices;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

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
            ClientServices.CLIENT.setMousePos(capturedX, capturedY);
        }
        captured = false;
    }
}
