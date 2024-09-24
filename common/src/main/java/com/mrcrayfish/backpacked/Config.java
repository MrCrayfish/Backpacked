package com.mrcrayfish.backpacked;

import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.backpacked.client.gui.ButtonAlignment;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.framework.api.config.BoolProperty;
import com.mrcrayfish.framework.api.config.ConfigProperty;
import com.mrcrayfish.framework.api.config.ConfigType;
import com.mrcrayfish.framework.api.config.DoubleProperty;
import com.mrcrayfish.framework.api.config.EnumProperty;
import com.mrcrayfish.framework.api.config.FrameworkConfig;
import com.mrcrayfish.framework.api.config.IntProperty;
import com.mrcrayfish.framework.api.config.ListProperty;
import com.mrcrayfish.framework.api.config.StringProperty;
import com.mrcrayfish.framework.api.config.event.FrameworkConfigEvents;
import com.mrcrayfish.framework.api.config.validate.Validator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class Config
{
    @FrameworkConfig(id = Constants.MOD_ID, name = "client", type = ConfigType.CLIENT)
    public static final Client CLIENT = new Client();

    @FrameworkConfig(id = Constants.MOD_ID, name = "server", type = ConfigType.SERVER_SYNC)
    public static final Server SERVER = new Server();

    public static class Client
    {
        @ConfigProperty(name = "hideConfigButton", comment = "If enabled, hides the config button from the backpack screen")
        public final BoolProperty hideConfigButton = BoolProperty.create(false);

        @ConfigProperty(name = "buttonAlignment", comment = "The alignment of the buttons in the backpack inventory screen")
        public final EnumProperty<ButtonAlignment> buttonAlignment = EnumProperty.create(ButtonAlignment.LEFT);
    }

    public static class Server
    {
        @ConfigProperty(name = "backpack")
        public final Backpack backpack = new Backpack();

        @ConfigProperty(name = "pickpocketing")
        public final Pickpocketing pickpocketing = new Pickpocketing();

        @ConfigProperty(name = "wanderingTrader")
        public final WanderingTrader wanderingTrader = new WanderingTrader();

        public static class Backpack
        {
            @ConfigProperty(name = "defaultCosmetic", comment = "The default cosmetic (model) of the backpack. This should generally be a backpack that is unlocked by default")
            public final StringProperty defaultCosmetic = StringProperty.create("backpacked:standard", new ResourceLocationValidator("Value needs to be a match an existing backpack"));

            @ConfigProperty(name = "keepOnDeath", comment = "If enabled, the backpack will stay on the player on death. Similar to keep inventory rule.")
            public final BoolProperty keepOnDeath = BoolProperty.create(false);

            @ConfigProperty(name = "inventorySizeColumns", comment = "The amount of slot columns in the backpack inventory.")
            public final IntProperty inventorySizeColumns = IntProperty.create(9, 1, BackpackContainerMenu.MAX_COLUMNS);

            @ConfigProperty(name = "inventorySizeRows", comment = "The amount of slot rows in the backpack inventory.")
            public final IntProperty inventorySizeRows = IntProperty.create(6, 1, BackpackContainerMenu.MAX_ROWS);

            @ConfigProperty(name = "disableCustomisation", comment = "If enabled, prevents backpacks from being customised. This will remove the customise button from the backpack inventory")
            public final BoolProperty disableCustomisation = BoolProperty.create(false);

            @ConfigProperty(name = "unlockAllCosmetics", comment = "Allows every player to use any backpack cosmetic variant without needing to complete the challenges. Side note, any progress to a challenge will not be tracked while enabled.")
            public final BoolProperty unlockAllCosmetics = BoolProperty.create(false);

            @ConfigProperty(name = "lockIntoSlot", comment = "Stops players from removing the backpack if it's not empty. This prevents players from carrying multiple backpacks.")
            public final BoolProperty lockIntoSlot = BoolProperty.create(true);

            @ConfigProperty(name = "autoEquipOnPickup", comment = "When picking up a backpack (with items inside) off the ground, the item will automatically equip. Having this enabled may not be ideal for multiplayer servers.")
            public final BoolProperty autoEquipOnPickup = BoolProperty.create(false);

            @ConfigProperty(name = "dropContentsFromShelf", comment = "When breaking a shelf, the placed backpack will also drops it's items into the world. This prevents players from carrying multiple backpacks")
            public final BoolProperty dropContentsFromShelf = BoolProperty.create(true);

            @ConfigProperty(name = "bannedItems", comment = "A list of items that are not allowed inside a backpack. Note: It is recommended to ban items that have an inventory as this will create large NBT data and potentially crash the server!")
            public final ListProperty<String> bannedItems = ListProperty.create(ListProperty.STRING, new ResourceLocationValidator("Value needs to be a valid item identifier"), Server::getDefaultBannedItems);
        }

        public static class Pickpocketing
        {
            @ConfigProperty(name = "enabledPickpocketing", comment = "If enabled, allows players to access the backpack of another player by interacting with the visible backpack on their back.")
            public final BoolProperty enabled = BoolProperty.create(true);

            @ConfigProperty(name = "maxReachDistance", comment = "The maximum reach distance of a player to interact with another player's backpack.")
            public final DoubleProperty maxReachDistance = DoubleProperty.create(2.0, 0.0, 4.0);

            @ConfigProperty(name = "maxAngleRange", comment = """
            The maximum angle at which another player's backpack can be accessed.
            Think of this as how directly behind the backpack the player needs to be
            in order to pickpocket. A smaller range prevents the player from accessing
            the backpack from the side.""")
            public final DoubleProperty maxRangeAngle = DoubleProperty.create(80.0, 0.0, 90.0);
        }

        public static class WanderingTrader
        {
            @ConfigProperty(name = "enableBackpack", comment = "If enabled, allows wandering traders to equip backpacks")
            public final BoolProperty enableBackpack = BoolProperty.create(true);

            @ConfigProperty(name = "spawnWithBackpackChance", comment = "The chance a Wandering Trader will spawn with a backpack. The chance is interpreted as one out of x, with x being the number given from this config option.")
            public final IntProperty spawnWithBackpackChance = IntProperty.create(2, 1, 100);

            @ConfigProperty(name = "pickpocketingChallenge")
            public final PickpocketingChallenge challenge = new PickpocketingChallenge();

            public static class PickpocketingChallenge
            {
                @ConfigProperty(name = "maxDetectionDistance", comment = "The maximum distance a Wandering Trader can detect a player. The longer the distance, the more difficult the challenge to pickpocket their backpack.")
                public final DoubleProperty maxDetectionDistance = DoubleProperty.create(10.0, 1.0, 32.0);

                @ConfigProperty(name = "timeToForgetPlayer", comment = "The time (in ticks) a Wandering Trader will wait before it decides to forget about a detected player. The Wandering Trader will wait indefinitely if the detected player is within the maximum detection distance.")
                public final IntProperty timeToForgetPlayer = IntProperty.create(200, 1, 12000);

                @ConfigProperty(name = "dislikedPlayersCanTrade", comment = "If true, allows players who are disliked by Wandering Traders to continue to trade normally with them. A player is considered disliked if they are caught when trying to pickpocket a Wandering Trader's backpack.")
                public final BoolProperty dislikedPlayersCanTrade = BoolProperty.create(false);

                @ConfigProperty(name = "dislikeCooldown", comment = "The amount of time (in ticks) a player has to wait before a Wandering Trader will like them again. If a player gets caught pickpocketing a Wandering Trader, the cooldown will be reset")
                public final IntProperty dislikeCooldown = IntProperty.create(6000, 0, 24000);

                @ConfigProperty(name = "generateEmeraldsOnly", comment = "Instead of generating trades as loot in the Wandering Traders backpacks, only generate emeralds.")
                public final BoolProperty generateEmeraldsOnly = BoolProperty.create(false);

                @ConfigProperty(name = "maxLootMultiplier", comment = "The maximum multiplier to apply when generating loot in the Wandering Trader backpack")
                public final IntProperty maxLootMultiplier = IntProperty.create(12, 1, 64);

                @ConfigProperty(name = "maxEmeraldStack", comment = "The maximum size of an emerald stack that can generate in the Wandering Trader backpack")
                public final IntProperty maxEmeraldStack = IntProperty.create(32, 1, 64);
            }
        }

        private static List<String> getDefaultBannedItems()
        {
            List<String> bannedItems = new ArrayList<>();
            bannedItems.add("travelersbackpack:custom_travelers_backpack");
            bannedItems.add("pinesbarrels:better_barrel");
            bannedItems.add("quark:seed_pouch");
            bannedItems.add("quark:backpack");
            bannedItems.add("sophisticatedbackpacks:backpack");
            bannedItems.add("sophisticatedbackpacks:iron_backpack");
            bannedItems.add("sophisticatedbackpacks:gold_backpack");
            bannedItems.add("sophisticatedbackpacks:diamond_backpack");
            bannedItems.add("sophisticatedbackpacks:netherite_backpack");
            bannedItems.add("improvedbackpacks:tiny_pocket");
            bannedItems.add("improvedbackpacks:medium_pocket");
            bannedItems.add("improvedbackpacks:large_pocket");
            bannedItems.add("improvedbackpacks:white_backpack");
            bannedItems.add("improvedbackpacks:orange_backpack");
            bannedItems.add("improvedbackpacks:magenta_backpack");
            bannedItems.add("improvedbackpacks:light_blue_backpack");
            bannedItems.add("improvedbackpacks:yellow_backpack");
            bannedItems.add("improvedbackpacks:lime_backpack");
            bannedItems.add("improvedbackpacks:pink_backpack");
            bannedItems.add("improvedbackpacks:gray_backpack");
            bannedItems.add("improvedbackpacks:light_gray_backpack");
            bannedItems.add("improvedbackpacks:cyan_backpack");
            bannedItems.add("improvedbackpacks:purple_backpack");
            bannedItems.add("improvedbackpacks:blue_backpack");
            bannedItems.add("improvedbackpacks:brown_backpack");
            bannedItems.add("improvedbackpacks:green_backpack");
            bannedItems.add("improvedbackpacks:red_backpack");
            bannedItems.add("improvedbackpacks:black_backpack");
            bannedItems.add("immersiveengineering:toolbox");
            bannedItems.add("immersiveengineering:crate");
            bannedItems.add("immersiveengineering:reinforced_crate");
            bannedItems.add("create:white_toolbox");
            bannedItems.add("create:orange_toolbox");
            bannedItems.add("create:magenta_toolbox");
            bannedItems.add("create:light_blue_toolbox");
            bannedItems.add("create:yellow_toolbox");
            bannedItems.add("create:lime_toolbox");
            bannedItems.add("create:pink_toolbox");
            bannedItems.add("create:gray_toolbox");
            bannedItems.add("create:light_gray_toolbox");
            bannedItems.add("create:cyan_toolbox");
            bannedItems.add("create:purple_toolbox");
            bannedItems.add("create:blue_toolbox");
            bannedItems.add("create:brown_toolbox");
            bannedItems.add("create:green_toolbox");
            bannedItems.add("create:red_toolbox");
            bannedItems.add("create:black_toolbox");
            bannedItems.add("mekanism:personal_chest");
            bannedItems.add("supplementaries:sack");
            return bannedItems;
        }
    }

    public static class ResourceLocationValidator implements Validator<String>
    {
        private final String hint;

        public ResourceLocationValidator(String hint)
        {
            this.hint = hint;
        }

        @Override
        public boolean test(String value)
        {
            return ResourceLocation.tryParse(value) != null;
        }

        @Override
        public Component getHint()
        {
            return Component.literal(this.hint);
        }
    }

    private static Set<ResourceLocation> bannedItemsList;

    public static void init()
    {
        FrameworkConfigEvents.LOAD.register(object -> {
            if(object == SERVER) {
                updateBannedItemsList();
            }
        });
        FrameworkConfigEvents.RELOAD.register(object -> {
            if(object == SERVER) {
                updateBannedItemsList();
            }
        });
    }

    public static void updateBannedItemsList()
    {
        bannedItemsList = ImmutableSet.copyOf(Config.SERVER.backpack.bannedItems.get().stream().map(ResourceLocation::new).collect(Collectors.toSet()));
    }

    public static Set<ResourceLocation> getBannedItemsList()
    {
        return bannedItemsList != null ? bannedItemsList : Collections.emptySet();
    }
}
