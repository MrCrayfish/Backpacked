package com.mrcrayfish.backpacked.common;

import java.util.function.Supplier;

public final class PlaceSoundControls
{
    private static final PlaceSoundControls INSTANCE = new PlaceSoundControls();

    private boolean enabled;
    private boolean aboutToPlay;
    private boolean preventNextPlay;
    private boolean sendToAllPlayers;

    public static <T> T runWithOptions(boolean preventNextPlay, boolean sendToAllPlayers, Supplier<T> action)
    {
        try
        {
            INSTANCE.enabled = true;
            INSTANCE.preventNextPlay = preventNextPlay;
            INSTANCE.sendToAllPlayers = sendToAllPlayers;
            return action.get();
        }
        finally
        {
            INSTANCE.enabled = false;
            INSTANCE.preventNextPlay = false;
            INSTANCE.sendToAllPlayers = false;
            INSTANCE.aboutToPlay = false;
        }
    }

    public static boolean isAboutToPlay()
    {
        return INSTANCE.enabled && INSTANCE.aboutToPlay;
    }

    public static boolean shouldPreventNextPlay()
    {
        return INSTANCE.enabled && INSTANCE.preventNextPlay;
    }

    public static boolean shouldSendToAllPlayers()
    {
        return INSTANCE.enabled && INSTANCE.sendToAllPlayers;
    }

    public static void markAboutToPlay()
    {
        if(INSTANCE.enabled)
        {
            INSTANCE.aboutToPlay = true;
        }
    }
}
