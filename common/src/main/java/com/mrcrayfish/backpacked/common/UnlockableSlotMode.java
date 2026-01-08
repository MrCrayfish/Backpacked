package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Constants;
import com.mrcrayfish.backpacked.client.SpriteProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;

public enum UnlockableSlotMode implements SpriteProvider
{
    ENABLED(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "backpack/lock_enabled"), ChatFormatting.GREEN, "backpacked.unlockable_slot_mode.enabled"),
    PURCHASABLE(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "backpack/lock_purchasable"), ChatFormatting.GOLD, "backpacked.unlockable_slot_mode.purchasable"),
    DISABLED(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "backpack/lock_disabled"), ChatFormatting.RED, "backpacked.unlockable_slot_mode.disabled");

    private final Identifier texture;
    private final ChatFormatting format;
    private final String key;

    UnlockableSlotMode(Identifier texture, ChatFormatting format, String key)
    {
        this.texture = texture;
        this.format = format;
        this.key = key;
    }

    @Override
    public Identifier getSprite(boolean active, boolean hovered)
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
