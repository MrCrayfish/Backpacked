package com.mrcrayfish.backpacked;

import com.google.common.collect.ImmutableSet;
import com.mrcrayfish.backpacked.common.*;
import com.mrcrayfish.backpacked.common.augment.impl.EmptyAugment;
import com.mrcrayfish.backpacked.core.ModRegistries;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.framework.api.config.*;
import com.mrcrayfish.framework.api.config.event.FrameworkConfigEvents;
import com.mrcrayfish.framework.api.config.validate.Validator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
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

    @FrameworkConfig(id = Constants.MOD_ID, name = "augments", type = ConfigType.SERVER_SYNC)
    public static final Augments AUGMENTS = new Augments();

    public static class Backpack
    {
        @ConfigProperty(name = "equipable", comment = "Equipable related properties")
        public final Equipable equipable = new Equipable();

        @ConfigProperty(name = "cosmetics", comment = "Cosmetic related properties")
        public final Cosmetics cosmetics = new Cosmetics();

        @ConfigProperty(name = "inventory", comment = "Inventory related properties")
        public final Inventory inventory = new Inventory();

        @ConfigProperty(name = "augmentBays", comment = "Augment Bay related properties")
        public final AugmentBays augmentBays = new AugmentBays();

        public static class Equipable
        {
            @ConfigProperty(name = "maxEquipable", comment = """
                    The maximum amount of backpacks that can be equipped by a player. This will determine
                    how many slots will appear when opening the "Equipped Backpacks" menu.""")
            public final IntProperty maxEquipable = IntProperty.create(5, 1, MAX_EQUIPPABLE_BACKPACKS);

            @ConfigProperty(name = "keepOnDeath", comment = """
                    If enabled, backpacks will stay equipped on the player after death (same as the
                    keepInventory game rule). Please note that this will make the Recall augment
                    effectively useless.""")
            public final BoolProperty keepOnDeath = BoolProperty.create(false);

            @ConfigProperty(name = "unlockFirstEquipableSlot", comment = """
                    If true, the first slot will automatically be unlocked by default and for free.""")
            public final BoolProperty unlockFirstEquipableSlot = BoolProperty.create(true);

            @ConfigProperty(name = "unlockAllEquipableSlots", comment = """
                    If set to true, all equipable slots will be unlocked by default.
                    WARNING: Reverting the option from true to false will cause backpacks to be dropped
                    into the world if the slot they are in is now locked. You have been warned.""")
            public final BoolProperty unlockAllEquipableSlots = BoolProperty.create(false);

            @ConfigProperty(name = "allowUnlockingUsingUnlockToken", comment = """
                    If set to true, equipable slots may be unlocked using Unlock Tokens""")
            public final BoolProperty allowUnlockingUsingUnlockToken = BoolProperty.create(false);

            @ConfigProperty(name = "unlockCost", comment = "Cost related properties for equipable slots")
            public final UnlockCost unlockCost = new UnlockCost(InterpolateFunction.LINEAR, 30, 30);
        }

        public static class Cosmetics
        {
            @ConfigProperty(name = "defaultCosmetic", comment = """
                    The default cosmetic (model) of the backpack. This should generally be a backpack
                    that is unlocked by default""")
            public final StringProperty defaultCosmetic = StringProperty.create("backpacked:vintage", new ResourceLocationValidator("Value needs to be a match an existing backpack"));

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

                @ConfigProperty(name = "allowUnlockingUsingUnlockToken", comment = """
                    If set to true, backpack slots may be unlocked using Unlock Tokens""")
                public final BoolProperty allowUnlockingUsingUnlockToken = BoolProperty.create(true);

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

        public static class AugmentBays
        {
            @ConfigProperty(name = "unlockFirstAugmentBay", comment = """
                    If true, the first augment bay will automatically be unlocked by default and for free.""")
            public final BoolProperty unlockFirstAugmentBay = BoolProperty.create(false);

            @ConfigProperty(name = "unlockAllAugmentBays", comment = """
                    If set to true, all augment bays will be unlocked by default.""")
            public final BoolProperty unlockAllAugmentBays = BoolProperty.create(false);

            @ConfigProperty(name = "allowUnlockingUsingUnlockToken", comment = """
                    If set to true, augment bays may be unlocked using Unlock Tokens""")
            public final BoolProperty allowUnlockingUsingUnlockToken = BoolProperty.create(false);

            @ConfigProperty(name = "unlockCost", comment = "Cost related properties for augment bays")
            public final UnlockCost unlockCost = new UnlockCost(List.of(10, 20, 30, 40), SelectionFunction.INDEX_WITH_CLAMP);
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
            public final BoolProperty useCustomCosts;

            @ConfigProperty(name = "customCosts", comment = """
                    A list of numbers that represent the cost values, must be whole and positive
                    numbers only. See customCostsSelectionFunction property to set the behaviour of
                    how values are selected from the custom costs list.
                    """)
            public final ListProperty<Integer> customCosts;

            @ConfigProperty(name = "customCostsSelectionFunction", comment = """
                    Determines how the cost value is selected from the customCosts list.
                    
                    Possible Functions:
                    LINEAR_INTERPOLATION - This will count how many slots/bays are unlocked plus one and divide it by the
                                           maximum unlockable slots/bays. For example, if 10 out of 20 slots are unlocked,
                                           a value of 11 divided by 20 will be evaluated and that will equal 0.55 or 55%.
                                           A cost value is then picked out of the customCosts list at the position that
                                           represents 55% percent of the lists length. So if customCosts contained 20 values, it
                                           will be picking the 11th value. The formula is custom_costs_length * ((unlocked_count + 1) / total_unlockable).
                                           (See below for real examples)
                    INDEX_WITH_CLAMP     - This option will exactly match the next unlock count to an index in the customCosts
                                           list. For example, if 10 out of 20 slots are unlocked, the next unlock count will
                                           be 11, so the 11th value in the custom costs list will be used as the cost to
                                           unlock the next slot/bay. This option will safely handle cases where if the 11th value
                                           doesn't exist, due the custom costs list containing less than 11 values, the last
                                           value in the list will be selected. (See below for real examples)
                    
                    Examples:
                    1. Backpack has 27 unlockable slots, and this option is set to INDEX_WITH_CLAMP. The custom costs
                    list contains the values [5, 5, 5, 5, 5, 10]. This is interpreted as "the first five unlock costs
                    will be 5, all the remaining will cost 10".
                    
                    2. Backpack has 3 unlockable augment bays, and this option is set to INDEX_WITH_CLAMP. The custom
                    cost lists contains the values [5, 10, 15]. This is interpreted as "the first augment bay will
                    cost 5, the second will cost 10, and the final will cost 15".
                    
                    3. Backpack has 18 unlockable slots, and this option is set to LINEAR_INTERPOLATION. The custom costs
                    list contains the values [1, 100]. This is interpreted as "the first nine unlock costs will
                    cost 1, then the final nine will cost 100".
                    
                    4. Backpack has 9 unlockable slots, and this option is set to LINEAR_INTERPOLATION, and the custom costs
                    list contains the values [1, 2, 3, 4, 5, 6, 7, 8, 9]. Since the count of the unlockable slots (9) and the
                    length of the customCosts list (9) are the exact same, the first unlock cost will be 1, the
                    second 2, the third 3, and so on until 9. However, if you bumped the backpack to 18 unlockable slots,
                    this would then change to, the first two unlock costs will be 1, the next two unlock costs will be 2, the
                    following two unlock costs will be 3.
                    
                    Note from MrCrayfish:
                    This may seem confusing (and it is), but just play around with this config property. Always start with a
                    fresh backpack everytime you change this property, or you may not see how the property affects the selection
                    process. I suggest starting with LINEAR_INTERPOLATION and then adding enough values to the customCosts list
                    so it matches the number of slots in the backpack (so if the backpack has 54 slots, put 54 values into customCosts).
                    Once you've tried that, half the number of values in customCosts (so if 54 slots, put 27 values into customCosts)
                    and this will give you an understanding of how the function works. Then afterwards, try INDEX_WITH_CLAMP but set
                    the values [1, 2, 3] as the customCosts. You will see the first unlock cost 1, the second cost 2, and every other
                    unlock will cost 3.
                    """)
            public final EnumProperty<SelectionFunction> customCostsSelectionFunction;

            public UnlockCost(InterpolateFunction defaultFunction, int defaultMinCost, int defaultMaxCost)
            {
                this.costInterpolateFunction = EnumProperty.create(defaultFunction);
                this.minCost = IntProperty.create(defaultMinCost, 1, 100);
                this.maxCost = IntProperty.create(defaultMaxCost, 1, 100);
                this.useCustomCosts = BoolProperty.create(false);
                this.customCosts = ListProperty.create(ListProperty.INT);
                this.customCostsSelectionFunction = EnumProperty.create(SelectionFunction.LINEAR_INTERPOLATION);
            }

            public UnlockCost(List<Integer> customCosts, SelectionFunction selectionFunction)
            {
                this.costInterpolateFunction = EnumProperty.create(InterpolateFunction.LINEAR);
                this.minCost = IntProperty.create(10, 1, 100);
                this.maxCost = IntProperty.create(40, 1, 100);
                this.useCustomCosts = BoolProperty.create(true);
                this.customCosts = ListProperty.create(ListProperty.INT, () -> customCosts);
                this.customCostsSelectionFunction = EnumProperty.create(selectionFunction);
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

            @Override
            public SelectionFunction getCustomCostsSelectionFunction()
            {
                return this.customCostsSelectionFunction.get();
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

        @ConfigProperty(name = "hideAddonsCallToAction", comment = """
                If enabled, hides the call to action at the bottom of the customisation menu which links out to community addons and a guide.""")
        public final BoolProperty hideAddonsCallToAction = BoolProperty.create(false);
    }

    public static class Augments
    {
        @ConfigProperty(name = "disabledAugments", comment = """
                A list that contains ids of augments that should be disabled. The augments will be
                hidden from the popup menu and cannot be selected. Augments applied on existing
                backpacks will simply be removed. Use advanced tooltips (F3 + H) to discover the IDs
                of augments.
                Example: disabledAugments = ["backpacked:recall", "backpacked:lootbound"]""")
        public final ListProperty<String> disabledAugments = ListProperty.create(ListProperty.STRING, new ResourceLocationValidator(""));

        @ConfigProperty(name = "funnelling", comment = "Funnelling related properties")
        public final Funnelling funnelling = new Funnelling();

        public static class Funnelling
        {
            @ConfigProperty(name = "maxFilters", comment = """
                    The maximum amount of filters that can be configured""")
            public final IntProperty maxFilters = IntProperty.create(32, 1, 256);
        }
    }

    public record ResourceLocationValidator(String hint) implements Validator<String>
    {
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

    public static final int MAX_EQUIPPABLE_BACKPACKS = 9;
    private static final PaymentItem INVENTORY_PAYMENT_ITEM = new PaymentItem(BACKPACK.inventory.slots.unlockCost.paymentItem::get);
    private static final PaymentItem BACKPACK_PAYMENT_ITEM = new PaymentItem(BACKPACK.equipable.unlockCost.paymentItem::get);
    private static final PaymentItem AUGMENT_BAY_PAYMENT_ITEM = new PaymentItem(BACKPACK.augmentBays.unlockCost.paymentItem::get);
    private static Set<ResourceLocation> bannedItemsList;
    private static Set<ResourceLocation> disabledAugments;

    public static void init()
    {
        FrameworkConfigEvents.LOAD.register(object -> {
            if(object == BACKPACK) {
                updateBannedItemsList();
                INVENTORY_PAYMENT_ITEM.clearItem();
                BACKPACK_PAYMENT_ITEM.clearItem();
                AUGMENT_BAY_PAYMENT_ITEM.clearItem();
            } else if(object == AUGMENTS) {
                disabledAugments = null;
            }
        });
        FrameworkConfigEvents.RELOAD.register(object -> {
            if(object == BACKPACK) {
                updateBannedItemsList();
                INVENTORY_PAYMENT_ITEM.clearItem();
                BACKPACK_PAYMENT_ITEM.clearItem();
                AUGMENT_BAY_PAYMENT_ITEM.clearItem();
            } else if(object == AUGMENTS) {
                disabledAugments = null;
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

    public static PaymentItem getAugmentBayPaymentItem()
    {
        return AUGMENT_BAY_PAYMENT_ITEM;
    }

    public static Set<ResourceLocation> getDisabledAugments()
    {
        if(disabledAugments == null)
        {
            disabledAugments = ImmutableSet.copyOf(Config.AUGMENTS.disabledAugments.get().stream()
                .map(ResourceLocation::tryParse)
                .filter(Objects::nonNull)
                .filter(ModRegistries.AUGMENT_TYPES::containsKey) // Ensure augment types exist
                .filter(id -> !EmptyAugment.TYPE.id().equals(id)) // Ensure empty augment is not disabled
                .collect(Collectors.toSet()));
        }
        return disabledAugments;
    }
}
