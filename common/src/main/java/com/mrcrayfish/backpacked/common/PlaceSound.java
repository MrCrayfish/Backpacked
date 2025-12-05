package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.common.augment.impl.LightweaverAugment;

public final class PlaceSound
{
    private static boolean disableNextPlay;
    private static boolean sendToAll;

    public static void update(LightweaverAugment augment)
    {
        disableNextPlay = !augment.sound();
        sendToAll = true;
    }

    public static void reset()
    {
        disableNextPlay = false;
        sendToAll = false;
    }

    public static boolean disableNextPlay()
    {
        return disableNextPlay;
    }

    public static boolean sendToAll()
    {
        return sendToAll;
    }
}
