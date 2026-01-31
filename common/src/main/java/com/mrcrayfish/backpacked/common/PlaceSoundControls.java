package com.mrcrayfish.backpacked.common;

import net.minecraft.world.item.context.UseOnContext;

import java.util.function.Supplier;

/**
 * Allows for the ability to control the place sound when calling {@link net.minecraft.world.item.BlockItem#useOn(UseOnContext)}.
 * When the mention method is called on the server only, as seen in {@link com.mrcrayfish.backpacked.common.augment.data.Farmhand}
 * handler, it doesn't send the place sound to the player that is planting the seed.
 * <p>
 * This class and with the help of the mixins {@link com.mrcrayfish.backpacked.mixin.common.LevelMixin}
 * and {@link com.mrcrayfish.backpacked.mixin.common.BlockItemMixin} are used to enable customisation
 * of the place sound, solving the aforementioned issue but also allow for the sound to be disabled.
 */
public final class PlaceSoundControls
{
    // Don't care about potential nested calls, so a singleton instance is used for everything
    // It is out of scope to solve edge cases that aren't going to be an issue in Backpacked
    private static final PlaceSoundControls INSTANCE = new PlaceSoundControls();

    // For setup
    private boolean enabled;
    private boolean aboutToPlay;

    // Options
    private boolean preventNextPlay;
    private boolean sendToAllPlayers;

    private PlaceSoundControls() {}

    public static <T> T runWithOptions(boolean preventNextPlay, boolean sendToAllPlayers, Supplier<T> action)
    {
        try
        {
            INSTANCE.enabled = true;
            INSTANCE.aboutToPlay = false;
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
