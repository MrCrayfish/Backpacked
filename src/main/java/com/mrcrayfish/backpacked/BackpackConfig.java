package com.mrcrayfish.backpacked;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Mod;

/**
 * Author: MrCrayfish
 */
@Config(modid = Reference.MOD_ID)
@Config.LangKey(Reference.MOD_ID + ".config.title")
public class BackpackConfig
{
    @Config.Name("Common")
    @Config.Comment("Common-only configs")
    @Config.LangKey(Reference.MOD_ID + ".config.common")
    public static final Common COMMON = new Common();

    public static class Common
    {
        @Config.Name("Keep Backpack on Death")
        @Config.Comment("Determines whether or not the backpack should be dropped on death")
        @Config.LangKey(Reference.MOD_ID + ".config.common.keepBackpackOnDeath")
        public boolean keepBackpackOnDeath = true;

        @Config.Name("Backpack Inventory Size")
        @Config.Comment("The amount of rows the backpack has. Each row is nine slots of storage.")
        @Config.LangKey(Reference.MOD_ID + ".config.common.backpackInventorySize")
        @Config.RangeInt(min = 1, max = 6)
        public int backpackInventorySize = 1;
    }
}
