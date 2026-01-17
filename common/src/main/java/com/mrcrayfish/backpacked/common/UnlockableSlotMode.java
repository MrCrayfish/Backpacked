package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.client.SpriteProvider;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;

public enum UnlockableSlotMode implements SpriteProvider
{
    ENABLED(Utils.id("backpack/lock_enabled"), ChatFormatting.GREEN, "backpacked.unlockable_slot_mode.enabled"),
    PURCHASABLE(Utils.id("backpack/lock_purchasable"), ChatFormatting.GOLD, "backpacked.unlockable_slot_mode.purchasable"),
    DISABLED(Utils.id("backpack/lock_disabled"), ChatFormatting.RED, "backpacked.unlockable_slot_mode.disabled");

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
