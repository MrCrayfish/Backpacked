package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.backpacked.util.Utils;
import com.mrcrayfish.framework.api.registry.RegistryContainer;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

@RegistryContainer(clientOnly = true)
public class ModKeyMappings
{
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Utils.id("backpacked"));
    public static final KeyMapping KEY_BACKPACK = new KeyMapping("key.backpacked.open_backpack", GLFW.GLFW_KEY_B, CATEGORY);
    public static final KeyMapping KEY_MANAGEMENT = new KeyMapping("key.backpacked.open_management", GLFW.GLFW_KEY_V, CATEGORY);
}
