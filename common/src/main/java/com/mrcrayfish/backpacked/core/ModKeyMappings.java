package com.mrcrayfish.backpacked.core;

import com.mrcrayfish.framework.api.registry.RegistryContainer;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

@RegistryContainer(clientOnly = true)
public class ModKeyMappings
{
    public static final KeyMapping KEY_BACKPACK = new KeyMapping("key.backpack", GLFW.GLFW_KEY_B, KeyMapping.Category.INVENTORY);
}
