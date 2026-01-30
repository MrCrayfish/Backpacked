package com.mrcrayfish.backpacked.common;

import net.minecraft.ChatFormatting;

public enum UnlockableSlotMode // TODO DONE
{
    ENABLED(ChatFormatting.GREEN, "backpacked.unlockable_slot_mode.enabled"),
    PURCHASABLE(ChatFormatting.GOLD, "backpacked.unlockable_slot_mode.purchasable"),
    DISABLED(ChatFormatting.RED, "backpacked.unlockable_slot_mode.disabled");

    private final ChatFormatting format;
    private final String key;

    UnlockableSlotMode(ChatFormatting format, String key)
    {
        this.format = format;
        this.key = key;
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
