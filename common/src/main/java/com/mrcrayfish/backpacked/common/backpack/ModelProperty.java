package com.mrcrayfish.backpacked.common.backpack;

/**
 * Author: MrCrayfish
 */
public enum ModelProperty
{
    SHOW_WITH_ELYTRA("AlwaysRenderBackpack", false),
    SHOW_EFFECTS("ShowBackpackEffects", true),
    SHOW_GLINT("ShowEnchantmentGlint", false);

    private final String tagName;
    private final boolean defaultValue;

    ModelProperty(String tagName, boolean defaultValue)
    {
        this.tagName = tagName;
        this.defaultValue = defaultValue;
    }

    public String getTagName()
    {
        return this.tagName;
    }

    public boolean getDefaultValue()
    {
        return this.defaultValue;
    }
}
