package com.mrcrayfish.backpacked.util;

/**
 * Author: MrCrayfish
 */
public class ScreenUtil
{
    public static boolean isPointInArea(int px, int py, int x, int y, int width, int height)
    {
        return px >= x && px < x + width && py >= y && py < y + height;
    }
}
