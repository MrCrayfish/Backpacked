package com.mrcrayfish.backpacked;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    public static class Server
    {
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> bannedItems;

        Server(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Common configuration settings").push("common");
            this.bannedItems = builder
                    .comment("A list of items that are not allowed inside a backpack. Note: It is recommended to ban items that have an inventory as this will create large NBT data and potentially crash the server!")
                    .defineList("bannedItems", Collections.emptyList(), o ->
                    {
                        try
                        {
                            //Only allow valid resource locations
                            ResourceLocation.tryCreate(o.toString());
                            return true;
                        }
                        catch(ResourceLocationException e)
                        {
                            return false;
                        }
                    });
            builder.pop();
        }
    }

    static final ForgeConfigSpec commonSpec;
    public static final Config.Common COMMON;

    static final ForgeConfigSpec serverSpec;
    public static final Config.Server SERVER;

    static
    {
        final Pair<Common, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(Config.Common::new);
        commonSpec = commonPair.getRight();
        COMMON = commonPair.getLeft();

        final Pair<Server, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(Config.Server::new);
        serverSpec = serverPair.getRight();
        SERVER = serverPair.getLeft();
    }
}
