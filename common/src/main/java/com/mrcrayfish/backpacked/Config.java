package com.mrcrayfish.backpacked;

import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.backpacked.common.*;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.framework.api.config.*;
import com.mrcrayfish.framework.api.config.event.FrameworkConfigEvents;
import com.mrcrayfish.framework.api.config.validate.Validator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

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

    @FrameworkConfig(id = Constants.MOD_ID, name = "backpack", type = ConfigType.SERVER_SYNC)
    public static final Backpack BACKPACK = new Backpack();

    @FrameworkConfig(id = Constants.MOD_ID, name = "pickpocketing", type = ConfigType.SERVER_SYNC)
    public static final Pickpocketing PICKPOCKETING = new Pickpocketing();

    @FrameworkConfig(id = Constants.MOD_ID, name = "wandering_trader", type = ConfigType.SERVER)
    public static final WanderingTrader WANDERING_TRADER = new WanderingTrader();

    public static class Backpack
    {
        @ConfigProperty(name = "equipable", comment = "Equipable related properties")
        public final Equipable equipable = new Equipable();

        @ConfigProperty(name = "cosmetics", comment = "Cosmetic related properties")
        public final Cosmetics cosmetics = new Cosmetics();

        @ConfigProperty(name = "inventory", comment = "Inventory related properties")
        public final Inventory inventory = new Inventory();

        public static class Equipable
        {
            @ConfigProperty(name = "maxEquipable", comment = """
                    The maximum amount of backpacks that can be equipped by a player. This will determine
                    how many slots will appear when opening the "Equipped Backpacks" menu.""")
            public final IntProperty maxEquipable = IntProperty.create(5, 1, 9);

            @ConfigProperty(name = "keepOnDeath", comment = """
                    If enabled, backpacks will stay equipped on the player after death (same as the
                    keepInventory game rule), otherwise backpacks will simply drop on the ground like
                    regular items.""")
            public final BoolProperty keepOnDeath = BoolProperty.create(false);

            @ConfigProperty(name = "unlockFirstEquipableSlot", comment = """
                    If true, the first slot will automatically be unlocked by default and for free.""")
            public final BoolProperty unlockFirstEquipableSlot = BoolProperty.create(true);

            @ConfigProperty(name = "unlockAllEquipableSlots", comment = """
                    If set to true, all equipable slots will be unlocked by default.
                    WARNING: Reverting the option from true to false will cause backpacks to be dropped
                    into the world if the slot they are in is now locked. You have been warned.""")
            public final BoolProperty unlockAllEquipableSlots = BoolProperty.create(false);

            @ConfigProperty(name = "unlockCost", comment = "Cost related properties for equipable slots")
            public final UnlockCost unlockCost = new UnlockCost(InterpolateFunction.LINEAR, 30, 30);
        }

        public static class Cosmetics
        {
            @ConfigProperty(name = "defaultCosmetic", comment = """
                    The default cosmetic (model) of the backpack. This should generally be a backpack
                    that is unlocked by default""")
            public final StringProperty defaultCosmetic = StringProperty.create("backpacked:standard", new ResourceLocationValidator("Value needs to be a match an existing backpack"));

            @ConfigProperty(name = "disableCustomisation", comment = """
                    If enabled, prevents backpacks from being customised. This will remove the
                    customise button from the backpack inventory""")
            public final BoolProperty disableCustomisation = BoolProperty.create(false);

            @ConfigProperty(name = "unlockAllCosmetics", comment = """
                    Allows every player to use any backpack cosmetic variant without needing to
                    complete the challenges. Side note, any progress to a challenge will not be
                    tracked while enabled.""")
            public final BoolProperty unlockAllCosmetics = BoolProperty.create(false);
        }

        public static class Inventory
        {
            @ConfigProperty(name = "bannedItems", comment = """
                    A list of items that are not allowed inside the inventory of a backpack.
                    Note: It is recommended to ban items that have an inventory as this will create
                    large NBT data and potentially crash the server!""")
            public final ListProperty<String> bannedItems = ListProperty.create(ListProperty.STRING, new ResourceLocationValidator("Value needs to be a valid item identifier"), Inventory::getDefaultBannedItems);

            @ConfigProperty(name = "slots", comment = "Slots related properties")
            public final Slots slots = new Slots();

            @ConfigProperty(name = "size", comment = "Size related properties")
            public final Size size = new Size();

            public static class Slots
            {
                @ConfigProperty(name = "unlockAllSlots", comment = """
                        If set to true, all backpacks slots will be unlocked by default.
                        WARNING: Reverting the option from true to false will cause items to be dropped
                        into the world if the slot they are in is now locked. You have been warned.""")
                public final BoolProperty unlockAllSlots = BoolProperty.create(false);

                @ConfigProperty(name = "unlockCost", comment = "Cost related properties for inventory slots")
                public final UnlockCost unlockCost = new UnlockCost(InterpolateFunction.CUBIC, 1, 50);
            }

            public static class Size
            {
                @ConfigProperty(name = "columns", comment = """
                        The amount of columns in the backpack inventory.
                        WARNING: Larger than 15 columns will start to cut off GUI elements when using auto GUI
                        scale. If you make the size of the backpack smaller, items in the backpack that no
                        longer fit will spawn into the world. Take care when changing this property on a
                        running game/server since the changes will be automatically reloaded upon saving this file.""")
                public final IntProperty columns = IntProperty.create(9, 1, BackpackContainerMenu.MAX_COLUMNS);

                @ConfigProperty(name = "rows", comment = """
                        The amount of rows in the backpack inventory.
                        WARNING: Larger than 6 rows will not fit on some resolutions when using auto GUI scale.
                        If you make the size of the backpack smaller, items in the backpack that no
                        longer fit will spawn into the world. Take care when changing this property on a
                        running game/server since the changes will be automatically reloaded upon saving this file.""")
                public final IntProperty rows = IntProperty.create(5, 1, BackpackContainerMenu.MAX_ROWS);
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

        public static class UnlockCost implements CostModel
        {
            @ConfigProperty(name = "paymentType", comment = """
                    The type of payment to use to unlock backpack slots. By default this value is set to EXPERIENCE,
                    which will use the players experience levels are used to unlock new slots. If value is set to
                    ITEM, this will instead consume a specified item from the player's inventory (including anything
                    placed in the backpack) to unlock new slots.""")
            public final EnumProperty<PaymentType> paymentType = EnumProperty.create(PaymentType.EXPERIENCE);

            @ConfigProperty(name = "paymentItem", comment = """
                    Only applicable if paymentType is set to ITEM. This option will control the item used when paying
                    to unlock new slots.""")
            public final StringProperty paymentItem = StringProperty.create("minecraft:emerald", new ResourceLocationValidator("Must be a valid resource location that matches the id of an item"));

            @ConfigProperty(name = "costInterpolateFunction", comment = """
                    The interpolate method to use when calculating the cost of unlocking a slot. The cost
                    of slots increases the more slots that are unlocked. This function determines how steep
                    the price will increase after each slot is unlocked. The interpolated value is calculated
                    using the minCost and maxCost.
                    
                    Function Descriptions:
                    LINEAR - A constant grow in the cost. Cost will jump by the same value after every slot unlocked.
                    SQUARED - Slowly scales the cost for about half, then cost will increase noticeably for the final half.
                    CUBIC - Very slowly scales the cost for about two thirds, then cost increases sharply for the final third.
                    
                    Note: This property has no effect if useCustomCosts is set to true
                    """)
            public final EnumProperty<InterpolateFunction> costInterpolateFunction;

            @ConfigProperty(name = "minCost", comment = """
                    The minimum cost to unlock a backpack slot. This value would be the cost
                    when unlocking the first slot in a backpack inventory. The cost to unlock
                    subsequent slots are interpolated from the this value to the maxCost, and
                    scaled by the scaleFunction.
                    
                    Note: This property has no effect if useCustomCosts is set to true""")
            public final IntProperty minCost;

            @ConfigProperty(name = "maxCost", comment = """
                    The maximum cost to unlock a backpack slot. This value would be the cost
                    when unlocking the final slot in a backpack inventory. The cost to unlock
                    prior slots are interpolated from the minCost to this value, and scaled
                    by the scaleFunction.
                    
                    Note: This property has no effect if useCustomCosts is set to true""")
            public final IntProperty maxCost;

            @ConfigProperty(name = "useCustomCosts", comment = """
                    If enabled, instead of using a cost that is calculated based on a minCost
                    and maxCost, custom costs allow the cost to be specified manually using
                    a list of values (see customCosts).""")
            public final BoolProperty useCustomCosts = BoolProperty.create(false);

            @ConfigProperty(name = "customCosts", comment = """
                    A list of values that represent the cost to unlock each slot. For example,
                    if the backpack has 27 inventory slots in total, this list can hold 27 values to
                    specify the cost. Unlocking the first slot, the cost will be the first value
                    in the list. Unlocking the next slot, the cost will be the next value in the
                    list, and so on. This gives full control over the cost to unlock each slot.
                    
                    If the list does not contain enough values to cover every slot, a value is instead
                    selected from first to last value based on how many slots are unlocked. For example,
                    if the list only contains the values [1, 5] but there are 30 inventory slots in the
                    backpack, then this will be interpreted as the first 15 slots costing 1, and the last
                    15 slots costing 5. If the values were [1, 3, 10] and again there are 30 inventory slots,
                    then this will be interpreted as the first 10 slots costing 1, the next 10 slots costing 3,
                    and the final 10 slots costing 10.""")
            public final ListProperty<Integer> customCosts = ListProperty.create(ListProperty.INT);

            public UnlockCost(InterpolateFunction defaultFunction, int defaultMinCost, int defaultMaxCost)
            {
                this.costInterpolateFunction =  EnumProperty.create(defaultFunction);
                this.minCost = IntProperty.create(defaultMinCost, 1, 100);
                this.maxCost = IntProperty.create(defaultMaxCost, 1, 100);
            }

            @Override
            public PaymentType getPaymentType()
            {
                return this.paymentType.get();
            }

            @Override
            public String getPaymentItemId()
            {
                return this.paymentItem.get();
            }

            @Override
            public InterpolateFunction getInterpolateFunction()
            {
                return this.costInterpolateFunction.get();
            }

            @Override
            public int getMinCost()
            {
                return this.minCost.get();
            }

            @Override
            public int getMaxCost()
            {
                return this.maxCost.get();
            }

            @Override
            public boolean useCustomCosts()
            {
                return this.useCustomCosts.get();
            }

            @Override
            public List<Integer> getCustomCosts()
            {
                return this.customCosts.get();
            }
        }
    }

    public static class Pickpocketing
    {
        @ConfigProperty(name = "enabledPickpocketing", comment = """
                If enabled, allows players to access the backpack of another player by interacting
                with the visible backpack on their back.""")
        public final BoolProperty enabled = BoolProperty.create(true);

        @ConfigProperty(name = "maxReachDistance", comment = """
                The maximum reach distance of a player to interact with another player's backpack.""")
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
        @ConfigProperty(name = "enableBackpack", comment = "If enabled, wandering traders will have a chance to spawn with a backpack")
        public final BoolProperty enableBackpack = BoolProperty.create(true);

        @ConfigProperty(name = "spawnWithBackpackChance", comment = """
                The chance a Wandering Trader will spawn with a backpack. The chance is interpreted
                as one out of x, with x being the number given from this config option.""")
        public final IntProperty spawnWithBackpackChance = IntProperty.create(2, 1, 100);

        @ConfigProperty(name = "pickpocketingChallenge")
        public final PickpocketingChallenge challenge = new PickpocketingChallenge();

        public static class PickpocketingChallenge
        {
            @ConfigProperty(name = "maxDetectionDistance", comment = """
                    The maximum distance a Wandering Trader can detect a player. The longer the
                    distance, the more difficult the challenge to pickpocket their backpack.""")
            public final DoubleProperty maxDetectionDistance = DoubleProperty.create(10.0, 1.0, 32.0);

            @ConfigProperty(name = "timeToForgetPlayer", comment = """
                    The time (in ticks) a Wandering Trader will wait before it decides to forget
                    about a detected player. The Wandering Trader will wait indefinitely if the
                    detected player is within the maximum detection distance.""")
            public final IntProperty timeToForgetPlayer = IntProperty.create(200, 1, 12000);

            @ConfigProperty(name = "dislikedPlayersCanTrade", comment = """
                    If true, allows players who are disliked by Wandering Traders to continue to
                    trade normally with them. A player is considered disliked if they are caught when
                    trying to pickpocket a Wandering Trader's backpack.""")
            public final BoolProperty dislikedPlayersCanTrade = BoolProperty.create(false);

            @ConfigProperty(name = "dislikeCooldown", comment = """
                    The amount of time (in ticks) a player has to wait before a Wandering Trader will
                    like them again. If a player gets caught pickpocketing a Wandering Trader, the
                    cooldown will be reset""")
            public final IntProperty dislikeCooldown = IntProperty.create(6000, 0, 24000);

            @ConfigProperty(name = "generateEmeraldsOnly", comment = """
                    Instead of generating trades as loot in the Wandering Traders backpacks, only generate emeralds.""")
            public final BoolProperty generateEmeraldsOnly = BoolProperty.create(false);

            @ConfigProperty(name = "maxLootMultiplier", comment = """
                    The maximum multiplier to apply when generating loot in the Wandering Trader backpack.""")
            public final IntProperty maxLootMultiplier = IntProperty.create(12, 1, 64);

            @ConfigProperty(name = "maxEmeraldStack", comment = """
                    The maximum size of an emerald stack that can generate in the Wandering Trader backpack.""")
            public final IntProperty maxEmeraldStack = IntProperty.create(32, 1, 64);
        }
    }

    public static class Client
    {
        @ConfigProperty(name = "hideConfigButton", comment = """
                If enabled, hides the config button from the backpack screen""")
        public final BoolProperty hideConfigButton = BoolProperty.create(false);

        @ConfigProperty(name = "unlockableSlotMode", comment = """
                Determines how unlockable slots are displayed and interactions are handled. This
                option can be changed directly in the backpack inventory GUI.
                
                Mode descriptions:
                ENABLED     - Unlockable slots will always be enabled (visible and interactable).
                PURCHASABLE - Unlockable slots will only be enabled if the player has has exp levels
                              to purchase/unlock the slot, otherwise they will be disabled (faded out
                              and not interactable).
                DISABLED    - Unlockable slots will always be disabled. (faded out and not interactable).
                """)
        public final EnumProperty<UnlockableSlotMode> unlockableSlotMode = EnumProperty.create(UnlockableSlotMode.ENABLED);

        @ConfigProperty(name = "glitterBomb", comment = """
                Very secret feature, do not enable if you don't want your screen filled with particle effects""")
        public final BoolProperty glitterBomb = BoolProperty.create(false);
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

    private static final PaymentItem INVENTORY_PAYMENT_ITEM = new PaymentItem(BACKPACK.inventory.slots.unlockCost.paymentItem::get);
    private static final PaymentItem BACKPACK_PAYMENT_ITEM = new PaymentItem(BACKPACK.equipable.unlockCost.paymentItem::get);
    private static Set<ResourceLocation> bannedItemsList;

    public static void init()
    {
        FrameworkConfigEvents.LOAD.register(object -> {
            if(object == BACKPACK) {
                updateBannedItemsList();
                INVENTORY_PAYMENT_ITEM.clearItem();
                BACKPACK_PAYMENT_ITEM.clearItem();
            }
        });
        FrameworkConfigEvents.RELOAD.register(object -> {
            if(object == BACKPACK) {
                updateBannedItemsList();
                INVENTORY_PAYMENT_ITEM.clearItem();
                BACKPACK_PAYMENT_ITEM.clearItem();
            }
        });
    }

    public static void updateBannedItemsList()
    {
        bannedItemsList = ImmutableSet.copyOf(Config.BACKPACK.inventory.bannedItems.get().stream().map(ResourceLocation::tryParse).collect(Collectors.toSet()));
    }

    public static Set<ResourceLocation> getBannedItemsList()
    {
        return bannedItemsList != null ? bannedItemsList : Collections.emptySet();
    }

    public static PaymentItem getInventoryPaymentItem()
    {
        return INVENTORY_PAYMENT_ITEM;
    }

    public static PaymentItem getBackpackPaymentItem()
    {
        return BACKPACK_PAYMENT_ITEM;
    }
}
