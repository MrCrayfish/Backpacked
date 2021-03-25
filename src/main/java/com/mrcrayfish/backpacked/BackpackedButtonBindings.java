package com.mrcrayfish.backpacked;

import com.mrcrayfish.controllable.client.BindingRegistry;
import com.mrcrayfish.controllable.client.ButtonBinding;
import net.minecraftforge.client.settings.KeyConflictContext;

/**
 * Author: MrCrayfish
 */
public class BackpackedButtonBindings
{
    public static final ButtonBinding BACKPACK = new ButtonBinding(-1, "backpacked.button.open_backpack", "button.categories.backpacked", KeyConflictContext.IN_GAME);

    public static void register()
    {
        BindingRegistry.getInstance().register(BACKPACK);
    }
}
