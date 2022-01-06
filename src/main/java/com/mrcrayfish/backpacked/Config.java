package com.mrcrayfish.backpacked;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.ResourceLocationException;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;

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
                    .define("keepBackpackOnDeath", false);
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
        public final ForgeConfigSpec.BooleanValue unlockAllBackpacks;
        public final ForgeConfigSpec.BooleanValue lockBackpackIntoSlot;
        public final ForgeConfigSpec.BooleanValue pickpocketBackpacks;
        public final ForgeConfigSpec.DoubleValue pickpocketMaxReachDistance;
        public final ForgeConfigSpec.DoubleValue pickpocketMaxRangeAngle;

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
                            ResourceLocation.tryParse(o.toString());
                            return true;
                        }
                        catch(ResourceLocationException e)
                        {
                            return false;
                        }
                    });
            this.unlockAllBackpacks = builder.comment("Allows every player to use any backpack cosmetic variant without needing to complete the challenges. Side note, any progress to a challenge will not be tracked while enabled.").define("unlockAllBackpacks", false);
            this.lockBackpackIntoSlot = builder.comment("Stops players from removing the backpack if it's not empty. This prevents players from carrying multiple backpacks").define("lockBackpackIntoSlot", true);
            this.pickpocketBackpacks = builder.comment("If enabled, allows players to access the backpack of another player by interacting with the visible backpack on their back.").define("pickpocketBackpacks", true);
            this.pickpocketMaxReachDistance = builder.comment("The maximum reach distance of a player to interact with another player's backpack.").defineInRange("pickpocketDistance", 1.5, 0.0, 4.0);
            this.pickpocketMaxRangeAngle = builder.comment("The maximum angle at which another player's backpack can be accessed").defineInRange("pickpocketMaxRangeAngle", 80.0, 0.0, 90.0);
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
