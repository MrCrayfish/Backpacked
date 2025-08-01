package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.SpriteProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

public enum UnlockableSlotMode implements SpriteProvider
{
    ENABLED(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/lock_enabled"), ChatFormatting.GREEN, "backpacked.unlockable_slot_mode.enabled"),
    PURCHASABLE(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/lock_purchasable"), ChatFormatting.GOLD, "backpacked.unlockable_slot_mode.purchasable"),
    DISABLED(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack/lock_disabled"), ChatFormatting.RED, "backpacked.unlockable_slot_mode.disabled");

    private final ResourceLocation texture;
    private final ChatFormatting format;
    private final String key;

    UnlockableSlotMode(ResourceLocation texture, ChatFormatting format, String key)
    {
        this.texture = texture;
        this.format = format;
        this.key = key;
    }

    @Override
    public ResourceLocation getSprite(boolean active, boolean hovered)
    {
        return this.texture;
    }

    public ChatFormatting getFormat()
    {
        return this.format;
    }

    public String getKey()
    {
        return this.key;
    }
}
