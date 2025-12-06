package com.mrcrayfish.backpacked.common.augment;

import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.common.PlaceSoundControls;
import com.mrcrayfish.backpacked.common.ShelfKey;
import com.mrcrayfish.backpacked.common.UseItemOnBlockFaceContext;
import com.mrcrayfish.backpacked.common.augment.data.Farmhand;
import com.mrcrayfish.backpacked.common.augment.data.Recall;
import com.mrcrayfish.backpacked.common.augment.impl.*;
import com.mrcrayfish.backpacked.core.ModAugmentTypes;
import com.mrcrayfish.backpacked.event.BackpackedEvents;
import com.mrcrayfish.backpacked.inventory.BackpackInventory;
import com.mrcrayfish.backpacked.mixin.common.BlockItemInvoker;
import com.mrcrayfish.backpacked.mixin.common.CropBlockMixin;
import com.mrcrayfish.backpacked.mixin.common.IntegerPropertyMixin;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageFarmhandPlant;
import com.mrcrayfish.backpacked.network.message.MessageLootboundTakeItem;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.backpacked.util.InventoryHelper;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import com.mrcrayfish.framework.api.network.LevelLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class AugmentHandler
{
    public static void init()
    {
        PlayerEvents.PICKUP_EXPERIENCE.register((player, orb) -> {
            AugmentHandler.onPlayerPickupExperienceOrb(player, orb);
            return false;
        });
        BackpackedEvents.MINED_BLOCK.register((snapshot, stack, player) -> {
           replantCrop(snapshot.state(), player, snapshot.pos());
        });
    }

    /**
     * Handles the Funnelling augment. When a player picks up an item entity, before adding the item
     * to the players inventory, any backpack that has the Funnelling augment will first attempt to
     * add the item to the backpack inventory, assuming the item also matches the user defined filters.
     *
     * @param player the player who is picking up the item
     * @param entity the item entity that is being picked up
     * @param target the target id of the item entity or null
     * @return Return true if vanilla handling should be cancelled
     */
    public static boolean beforeItemPickup(Player player, ItemEntity entity, @Nullable UUID target)
    {
        // Don't handle is item is empty
        ItemStack stack = entity.getItem();
        if(stack.isEmpty())
            return false;

        // Don't handle if a delay is present
        if(entity.hasPickUpDelay())
            return false;

        // Don't handle if the pickup target does not match the player
        if(target != null && !target.equals(player.getUUID()))
            return false;

        Item originalItem = stack.getItem();
        FunnelResult result = funnelItemStackIntoBackpack(player, stack);

        // If at least one item was funnelled into the backpack, run vanilla calls
        int funnelCount = result.funnelCount();
        if(funnelCount > 0)
        {
            player.take(entity, funnelCount);
            player.awardStat(Stats.ITEM_PICKED_UP.get(originalItem), funnelCount);
            player.onItemPickup(entity);
        }

        // If the entire stack was funnelled, cancel further handling and discard the item entity
        if(!result.hasRemaining() && result.funnelCount() > 0)
        {
            entity.discard();
            return true;
        }

        // Otherwise remaining stack will be put into inventory as normal
        return false;
    }

    /**
     * Handles the Funnelling augment when picking up arrows.
     *
     * @param player the player picking up the arrow
     * @param arrow  the arrow being picked up
     * @return True if further handing should be cancelled
     */
    public static boolean beforeArrowPickup(Player player, AbstractArrow arrow)
    {
        ItemStack stack = arrow.getPickupItemStackOrigin().copy();
        FunnelResult result = funnelItemStackIntoBackpack(player, stack);

        // If entire stack was funnelled, return true and prevent vanilla handling
        if(!result.hasRemaining())
            return true;

        // Rare case the stack is partially funnelled. Arrows in vanilla have a count of 1 but some mods might do weird stuff
        // Just try and add the remaining to inventory, don't care about if it was added or not
        if(result.funnelCount() > 0)
        {
            player.getInventory().add(stack);
            return true;
        }

        return false;
    }

    /**
     * Attempts to funnel the given stack into the backpack only if the backpack has the funnelling
     * augment. A result of the funnelling action will be returned after this method is called.
     * <p>
     * In the case no backpacks has the funnelling augment, a {@link FunnelResult#IGNORE} will be
     * returned; This means nothing happen and vanilla behaviour should run as normal. If the stack
     * was partially funnelled, for example only 2 of the 10 items were put into the backpacks,
     * {@link FunnelResult#hasRemaining} will be true and {@link FunnelResult#funnelCount} will be
     * assigned 2. If the entire stack was funnelled into the backpacks, {@link FunnelResult#hasRemaining}
     * will be false and {@link FunnelResult#funnelCount} will be equal to the count of the given
     * stack before it was funnelled into the backpacks.
     *
     * @param player the player which holds the backpacks the item should funnel into
     * @param stack  the stack to funnell into the backpacks
     * @return A result of the funnelling action (see docs above)
     */
    private static FunnelResult funnelItemStackIntoBackpack(Player player, ItemStack stack)
    {
        // Get backpacks with the Funnelling augment
        var snapshots = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.FUNNELLING.get());
        if(snapshots.isEmpty())
            return FunnelResult.IGNORE;

        // Iterate through the backpacks and attempt to funnel into their inventories
        int funnelCount = 0;
        for(var snapshot : snapshots)
        {
            if(snapshot.augment().test(stack))
            {
                int beforeCount = stack.getCount();
                ItemStack remaining = snapshot.inventory().addItem(stack);
                stack.setCount(remaining.getCount());
                funnelCount += (beforeCount - remaining.getCount());
                if(stack.isEmpty())
                {
                    break;
                }
            }
        }

        return new FunnelResult(!stack.isEmpty(), funnelCount);
    }

    private record FunnelResult(boolean hasRemaining, int funnelCount)
    {
        private static final FunnelResult IGNORE = new FunnelResult(false, 0);
    }

    public static ItemStack locateAmmunition(Player player, ItemStack weapon, ItemStack ammo)
    {
        if(!weapon.isEmpty() && weapon.getItem() instanceof ProjectileWeaponItem)
        {
            var snapshots = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.QUIVERLINK.get());
            for(var snapshot : snapshots)
            {
                if(!ammo.isEmpty() && snapshot.augment().priority() != QuiverlinkAugment.Priority.BACKPACK)
                    continue;

                BackpackInventory inventory = snapshot.inventory();
                Predicate<ItemStack> predicate = Services.PLATFORM.getValidProjectiles(weapon);
                ItemStack projectile = InventoryHelper.streamFor(inventory).filter(predicate).findFirst().orElse(ItemStack.EMPTY);
                if(!projectile.isEmpty())
                {
                    return projectile;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public static void onLootDroppedByEntity(Collection<ItemEntity> drops, Player player)
    {
        var snapshots = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.FUNNELLING.get(), ModAugmentTypes.LOOTBOUND.get());
        for(var snapshot : snapshots)
        {
            LootboundAugment augment = snapshot.secondAugment();
            if(!augment.mobs())
                continue;

            funnelDropsIntoBackpack(drops, player);
        }
    }

    public static void onLootDroppedByBlock(Collection<ItemEntity> drops, Player player)
    {
        var snapshots = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.FUNNELLING.get(), ModAugmentTypes.LOOTBOUND.get());
        for(var snapshot : snapshots)
        {
            LootboundAugment augment = snapshot.secondAugment();
            if(!augment.blocks())
                continue;

            funnelDropsIntoBackpack(drops, player);
        }
    }

    private static void funnelDropsIntoBackpack(Collection<ItemEntity> drops, Player player)
    {
        List<Pair<ItemStack, Vec3>> consumed = new ArrayList<>();
        drops.removeIf(drop -> {
            ItemStack copy = drop.getItem().copy();
            FunnelResult result = funnelItemStackIntoBackpack(player, copy);
            if(result.funnelCount() > 0) {
                ItemStack stack = drop.getItem().copyWithCount(result.funnelCount());
                consumed.add(Pair.of(stack, drop.position()));
            }
            drop.setItem(copy);
            return copy.isEmpty();
        });

        if(player.level() instanceof ServerLevel level)
        {
            consumed.forEach(pair -> {
                LevelLocation location = LevelLocation.create(level, pair.getSecond(), 32);
                Network.getPlay().sendToNearbyPlayers(() -> location, new MessageLootboundTakeItem(player.getId(), pair.getFirst(), pair.getSecond(), true));
            });
        }
    }

    public static void onPlayerPickupExperienceOrb(Player player, ExperienceOrb orb)
    {
        if(orb.isRemoved())
            return;

        Holder<Enchantment> mending = player.level().holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.MENDING);
        var snapshots = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.REFORGE.get());
        for(var snapshot : snapshots)
        {
            InventoryHelper.streamFor(snapshot.inventory()).filter(stack -> {
                return stack.getEnchantments().getLevel(mending) > 0 && stack.isDamageableItem() && stack.isDamaged() && stack.getCount() == 1 && stack.getMaxStackSize() == 1 && Services.PLATFORM.isRepairable(stack);
            }).forEach(stack -> {
                int repairableAmount = EnchantmentHelper.modifyDurabilityToRepairFromXp((ServerLevel) player.level(), stack, orb.getValue());
                int maxRepairableDamage = Math.min(repairableAmount, stack.getDamageValue());
                stack.setDamageValue(stack.getDamageValue() - maxRepairableDamage);
            });
        }
    }

    public static ItemStack locateTotemOfUndying(Player player)
    {
        var snapshots = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.IMMORTAL.get());
        for(var snapshot : snapshots)
        {
            BackpackInventory inventory = snapshot.inventory();
            for(int i = 0; i < inventory.getContainerSize(); i++)
            {
                ItemStack stack = inventory.getItem(i);
                if(stack.is(Items.TOTEM_OF_UNDYING))
                {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public static void onPlayerChangedBlockPos(ServerPlayer player, ServerLevel level, BlockPos pos, int brightness)
    {
        onPlayerWalkOnCrops(player);

        var snapshots = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.LIGHTWEAVER.get());
        for(var snapshot : snapshots)
        {
            LightweaverAugment augment = snapshot.augment();
            if(brightness > augment.minimumLight())
                return;

            ItemStack torch = snapshot.inventory().findFirst(stack -> stack.is(Items.TORCH));
            if(!torch.isEmpty())
            {
                InteractionResult result = PlaceSoundControls.runWithOptions(!augment.sound(), true, () -> {
                    return torch.useOn(UseItemOnBlockFaceContext.create(level, player, torch, pos.below(), Direction.UP));
                });
                if(result.consumesAction())
                {
                    snapshot.inventory().setChanged();
                    return;
                }
            }
        }
    }

    private static void replantCrop(BlockState state, ServerPlayer player, BlockPos pos)
    {
        if(!isFullyGrownCrop(state))
            return;

        Item seedItem = getCropSeed(player.level(), pos, state);
        if(seedItem == null)
            return;

        var snapshots = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.FARMHAND.get());
        if(snapshots.isEmpty())
            return;

        ServerLevel level = player.serverLevel();
        if(!level.getBlockState(pos).canBeReplaced())
            return;

        Farmhand farmhand = ((Farmhand.Access) level).backpacked$getFarmhand();
        if(farmhand.isPlanting(pos))
            return;

        for(var snapshot : snapshots)
        {
            BackpackInventory inventory = snapshot.inventory();
            ItemStack seed = inventory.findFirst(stack -> stack.is(seedItem));
            if(seed.isEmpty())
                continue;

            ItemStack copy = seed.copyWithCount(1);
            if(!farmhand.plant(copy, pos, player))
                continue;

            // Send particles to players
            var message = new MessageFarmhandPlant(copy, player.getId(), pos);
            Network.getPlay().sendToTrackingEntity(() -> player, message);
            Network.getPlay().sendToPlayer(() -> player, message);

            // Finally shrink the stack and remove the block position
            seed.shrink(1);
            inventory.setChanged();
            break;
        }
    }

    private static void onPlayerWalkOnCrops(ServerPlayer player)
    {
        var snapshots = BackpackHelper.getBackpackInventoriesWithAugment(player, ModAugmentTypes.SEEDFLOW.get());
        if(snapshots.isEmpty())
            return;

        // Create a list of possible block positions to place seeds
        List<BlockPos> positions = new ArrayList<>();
        Vec3 position = player.position().add(player.getForward().multiply(1, 0, 1).normalize());
        positions.add(BlockPos.containing(position.x - 0.5, position.y + 0.5, position.z - 0.5));
        positions.add(BlockPos.containing(position.x + 0.5, position.y + 0.5, position.z - 0.5));
        positions.add(BlockPos.containing(position.x + 0.5, position.y + 0.5, position.z + 0.5));
        positions.add(BlockPos.containing(position.x - 0.5, position.y + 0.5, position.z + 0.5));

        // Remove positions that are not possible to place a block
        ServerLevel level = player.serverLevel();
        Farmhand farmhand = ((Farmhand.Access) level).backpacked$getFarmhand();
        positions.removeIf(pos -> !level.getBlockState(pos).canBeReplaced() || farmhand.isPlanting(pos));

        // Leave early if no available block positions
        if(positions.isEmpty())
            return;

        for(var snapshot : snapshots)
        {
            SeedflowAugment augment = snapshot.augment();
            BackpackInventory inventory = snapshot.inventory();

            // This function supplies an itemstack of plantable seed from the backpack inventory
            boolean random = augment.randomizeSeeds();
            Function<BlockPos, ItemStack> seedSupplier = random
                    ? nextRandomizedPlantableSeed(level, augment, inventory)
                    : nextPlantableSeed(level, augment, inventory);

            boolean changed = false;
            ItemStack stack = ItemStack.EMPTY;
            Iterator<BlockPos> it = positions.iterator();
            while(it.hasNext())
            {
                BlockPos pos = it.next();
                if(stack.isEmpty() || random)
                    stack = seedSupplier.apply(pos);

                // If stack is still empty even after calling the supplier, there are no more seeds
                if(stack.isEmpty())
                    break;

                ItemStack copy = stack.copyWithCount(1);
                if(!farmhand.plant(copy, pos, player))
                    continue;

                // Send particles to players
                var message = new MessageFarmhandPlant(copy, player.getId(), pos);
                Network.getPlay().sendToTrackingEntity(() -> player, message);
                Network.getPlay().sendToPlayer(() -> player, message);

                // Finally shrink the stack and remove the block position
                stack.shrink(1);
                it.remove();
                changed = true;
            }

            // Send event to inventory if something changed
            if(changed)
            {
                inventory.setChanged();
            }

            // Can no longer play any more crops if there are no more available positions
            if(positions.isEmpty())
                return;
        }
    }

    private static boolean canUseBlockItemOnBlockPos(ServerLevel level, ItemStack stack, BlockPos pos, Direction face)
    {
        BlockItem item = (BlockItem) stack.getItem();
        Block block = item.getBlock();
        if(!block.isEnabled(level.enabledFeatures()))
            return false;

        var context = new BlockPlaceContext(UseItemOnBlockFaceContext.create(level, stack, pos, face));
        if(!context.canPlace())
            return false;

        context = item.updatePlacementContext(context);
        if(context == null)
            return false;

        BlockState state = ((BlockItemInvoker) item).backpacked$getPlacementState(context);
        return state != null;
    }

    private static boolean isFullyGrownCrop(BlockState state)
    {
        if(state.getBlock() instanceof BushBlock)
        {
            // Use max age method from crop blocks
            if(state.getBlock() instanceof CropBlock crop)
            {
                return crop.isMaxAge(state);
            }

            // Otherwise try checking if the age property is max value
            for(IntegerProperty property : SeedflowAugment.AGE_PROPERTIES)
            {
                if(state.hasProperty(property))
                {
                    return state.getValue(property) == ((IntegerPropertyMixin) property).backpacked$getMax();
                }
            }

            // Sometimes mods create their own age property (e.g. farmers delight)
            for(Property<?> property : state.getProperties())
            {
                if(property instanceof IntegerProperty integerProperty && property.getName().equals("age"))
                {
                    return state.getValue(integerProperty) == ((IntegerPropertyMixin) property).backpacked$getMax();
                }
            }
        }
        return false;
    }

    @Nullable
    private static Item getCropSeed(LevelReader reader, BlockPos pos, BlockState state)
    {
        if(state.getBlock() instanceof BushBlock bush)
        {
            if(bush instanceof CropBlock crop)
            {
                return ((CropBlockMixin) crop).backpacked$getBaseSeedId().asItem();
            }
            return bush.getCloneItemStack(reader, pos, state).getItem();
        }
        return null;
    }

    private static Function<BlockPos, ItemStack> nextPlantableSeed(ServerLevel level, SeedflowAugment augment, BackpackInventory inventory)
    {
        int[] currentIndex = {0};
        return pos -> {
            if(currentIndex[0] >= inventory.getContainerSize())
                return ItemStack.EMPTY;
            while(currentIndex[0] < inventory.getContainerSize()) {
                ItemStack stack = inventory.getItem(currentIndex[0]++);
                if(stack.isEmpty())
                    continue;
                if(!SeedflowAugment.ITEM_PLACES_AGEABLE_CROP.test(stack.getItem()))
                    continue;
                if(augment.useFilters() && !augment.isFilteringItem(stack.getItem()))
                    continue;
                if(!canUseBlockItemOnBlockPos(level, stack, pos, Direction.UP))
                    continue;
                return stack;
            }
            return ItemStack.EMPTY;
        };
    }

    private static Function<BlockPos, ItemStack> nextRandomizedPlantableSeed(ServerLevel level, SeedflowAugment augment, BackpackInventory inventory)
    {
        return pos -> {
            int count = 0;
            ItemStack result = ItemStack.EMPTY;
            for(int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if(stack.isEmpty())
                    continue;
                if(!SeedflowAugment.ITEM_PLACES_AGEABLE_CROP.test(stack.getItem()))
                    continue;
                if(augment.useFilters() && !augment.isFilteringItem(stack.getItem()))
                    continue;
                if(!canUseBlockItemOnBlockPos(level, stack, pos, Direction.UP))
                    continue;
                count++;
                if(level.random.nextInt(count) == 0) {
                    result = stack;
                }
            }
            return result;
        };
    }

    public static boolean recallBackpack(ServerPlayer player, int index, ItemStack stack, RecallAugment augment)
    {
        Optional<ShelfKey> optional = augment.shelfKey();
        if(optional.isEmpty())
            return false;

        MinecraftServer server = player.getServer();
        if(server == null)
            return false;

        ShelfKey shelfKey = optional.get();
        ServerLevel level = server.getLevel(shelfKey.level());
        if(level == null)
            return false;

        Recall recall = ((Recall.Access) level).backpacked$getRecall();
        return recall.recallToShelf(player, shelfKey, index, stack);
    }

    /*private static boolean isFarmland()
    {
        // Only allow blocks that can be planted on farmland
        BlockState farmlandState = Blocks.FARMLAND.defaultBlockState();
        if(!((BushBlockMixin) bush).backpacked$mayPlaceOn(farmlandState, EmptyBlockGetter.INSTANCE, BlockPos.ZERO))
            return false;
    }*/
}
