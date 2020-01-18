package com.mrcrayfish.backpacked;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Author: MrCrayfish
 */
public class Config
{
    public static class Common
    {
        public final ForgeConfigSpec.BooleanValue keepBackpackOnDeath;
        public final ForgeConfigSpec.IntValue backpackInventorySize;

        Common(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Common configuration settings").push("common");
            this.keepBackpackOnDeath = builder
                    .comment("Determines whether or not the backpack should be dropped on death")
                    .translation("backpacked.configgui.keepBackpackOnDeath")
                    .define("keepBackpackOnDeath", true);
            this.backpackInventorySize = builder
                    .comment("The amount of rows the backpack has. Each row is nine slots of storage.")
                    .translation("backpacked.configgui.backpackInventorySize")
                    .defineInRange("backpackInventorySize", 1, 1, 6);
            builder.pop();
        }
    }

    static final ForgeConfigSpec commonSpec;
    public static final Config.Common COMMON;

    static
    {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config.Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }
}
