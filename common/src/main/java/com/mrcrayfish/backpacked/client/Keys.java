package com.mrcrayfish.backpacked.client;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/**
 * Author: MrCrayfish
 */
public class Keys
{
    public static final KeyMapping KEY_BACKPACK = new KeyMapping("key.backpacked.open_backpack", GLFW.GLFW_KEY_B, "key.categories.backpacked");
    public static final KeyMapping KEY_MANAGEMENT = new KeyMapping("key.backpacked.open_management", GLFW.GLFW_KEY_V, "key.categories.backpacked");
}
