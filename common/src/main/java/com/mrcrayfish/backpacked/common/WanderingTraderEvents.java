package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.BackpackHelper;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.augment.Augments;
import com.mrcrayfish.backpacked.common.backpack.UnlockableSlots;
import com.mrcrayfish.backpacked.data.pickpocket.TraderPickpocketing;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncVillagerBackpack;
import com.mrcrayfish.backpacked.platform.Services;
import com.mrcrayfish.framework.api.event.EntityEvents;
import com.mrcrayfish.framework.api.event.PlayerEvents;
import com.mrcrayfish.framework.api.event.TickEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Author: MrCrayfish
 */
public class WanderingTraderEvents
{
    public static final Component WANDERING_BAG_TRANSLATION = Component.translatable("backpack.backpacked.wandering_bag");

    public static void init()
    {
        EntityEvents.JOIN_LEVEL.register(WanderingTraderEvents::onEntityJoinLevel);
        PlayerEvents.START_TRACKING_ENTITY.register(WanderingTraderEvents::onStartTracking);
        TickEvents.START_LIVING_ENTITY.register(WanderingTraderEvents::onTickLivingEntity);
    }

    private static void onEntityJoinLevel(Entity entity, Level level, boolean disk)
    {
        if(!entity.level().isClientSide() && entity instanceof WanderingTrader trader && Config.WANDERING_TRADER.enableBackpack.get())
        {
            TraderPickpocketing.get(trader).ifPresent(data ->
            {
                if(!data.isInitialized())
                {
                    boolean equipped = trader.level().random.nextInt(Config.WANDERING_TRADER.spawnWithBackpackChance.get()) == 0;
                    data.setBackpackEquipped(equipped);
                    data.setInitialized();
                }
            });
            patchTraderAiGoals(trader);
        }
    }

    private static void onStartTracking(Entity entity, Player player)
    {
        if(entity.getType() != EntityType.WANDERING_TRADER)
            return;

        WanderingTrader trader = (WanderingTrader) entity;
        TraderPickpocketing.get(trader).ifPresent(data ->
        {
            if(data.isBackpackEquipped())
            {
                Network.getPlay().sendToPlayer(() -> (ServerPlayer) player, new MessageSyncVillagerBackpack(entity.getId()));
            }
        });
    }

    private static void onTickLivingEntity(LivingEntity entity)
    {
        Level level = entity.level();
        if(level.isClientSide() || entity.getType() != EntityType.WANDERING_TRADER)
            return;

        WanderingTrader trader = (WanderingTrader) entity;
        if(trader.getUnhappyCounter() > 0)
        {
            trader.setUnhappyCounter(trader.getUnhappyCounter() - 1);
        }

        TraderPickpocketing.get(entity).ifPresent(data ->
        {
            if(!data.isBackpackEquipped())
                return;
            Map<Player, Long> detectedPlayers = data.getDetectedPlayers();
            List<Player> newDetectedPlayers = findDetectedPlayers(trader);
            newDetectedPlayers.forEach(player -> detectedPlayers.put(player, level.getGameTime()));
            detectedPlayers.entrySet().removeIf(createForgetPlayerPredicate(trader, level));
            data.getDislikedPlayers().entrySet().removeIf(entry -> level.getGameTime() - entry.getValue() > Config.WANDERING_TRADER.challenge.dislikeCooldown.get());
        });
    }

    public static boolean onInteract(Entity target, Player player)
    {
        Level level = target.level();
        if(!level.isClientSide() && target instanceof WanderingTrader trader)
        {
            if(!Config.WANDERING_TRADER.challenge.dislikedPlayersCanTrade.get() && TraderPickpocketing.get(trader).map(data -> data.isBackpackEquipped() && data.isDislikedPlayer(player)).orElse(false))
            {
                trader.setUnhappyCounter(20);
                level.playSound(null, trader, SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.5F);
                return true;
            }
        }
        return false;
    }

    private static List<Player> findDetectedPlayers(LivingEntity entity)
    {
        return entity.level().getEntities(EntityType.PLAYER, entity.getBoundingBox().inflate(getMaxDetectionDistance()), player -> {
            return isPlayerInLivingEntityVision(entity, player) && isPlayerSeenByLivingEntity(entity, player, getMaxDetectionDistance()) || !player.isCrouching() && isPlayerMoving(player);
        });
    }

    private static Predicate<Map.Entry<Player, Long>> createForgetPlayerPredicate(WanderingTrader trader, Level world)
    {
        return entry -> !entry.getKey().isAlive() || entry.getKey().distanceTo(trader) > getMaxDetectionDistance() * 2.0 || (world.getGameTime() - entry.getValue() > Config.WANDERING_TRADER.challenge.timeToForgetPlayer.get() && entry.getKey().distanceTo(trader) >= Config.WANDERING_TRADER.challenge.maxDetectionDistance.get());
    }

    // Determines if the player is in the living entities vision
    private static boolean isPlayerInLivingEntityVision(LivingEntity entity, Player player)
    {
        // Prevents the entity from seeing invisible players. Armour must be off too
        if(isPlayerInvisible(player))
            return false;
        Vec3 between = entity.position().subtract(player.position());
        float angle = (float) Math.toDegrees(Mth.atan2(between.z, between.x)) - 90F;
        return Mth.degreesDifferenceAbs(entity.yHeadRot + 180F, angle) <= 90F;
    }

    private static boolean isPlayerSeenByLivingEntity(LivingEntity entity, Player player, double distance)
    {
        // Prevents the entity from seeing invisible players. Armour must be off too
        if(isPlayerInvisible(player))
            return false;

        if(entity.level() != player.level() || entity.distanceTo(player) > distance)
            return false;

        Vec3 livingEyePos = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
        Vec3 playerEyePos = new Vec3(player.getX(), player.getEyeY(), player.getZ());
        if(performRayTrace(livingEyePos, playerEyePos, entity).getType() == BlockHitResult.Type.MISS)
            return true;

        Vec3 playerLegPos = new Vec3(player.getX(), player.getY() + 0.5, player.getZ());
        return performRayTrace(livingEyePos, playerLegPos, entity).getType() == BlockHitResult.Type.MISS;
    }

    private static boolean isPlayerInvisible(Player player)
    {
        return player.hasEffect(MobEffects.INVISIBILITY) && player.getArmorCoverPercentage() <= 0 && StreamSupport.stream(player.getHandSlots().spliterator(), false).allMatch(ItemStack::isEmpty) && BackpackHelper.getFirstBackpackStack(player).isEmpty();
    }

    private static BlockHitResult performRayTrace(Vec3 start, Vec3 end, Entity source)
    {
        return source.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, source));
    }

    private static boolean isPlayerMoving(Player player)
    {
        return ((IMovedAccess) player).backpacked$Moved();
    }

    public static void openBackpack(WanderingTrader trader, ServerPlayer openingPlayer)
    {
        TraderPickpocketing.get(trader).ifPresent(data ->
        {
            if(!data.isBackpackEquipped())
                return;

            if(data.getDetectedPlayers().containsKey(openingPlayer))
            {
                trader.setUnhappyCounter(20);
                trader.getLookControl().setLookAt(openingPlayer.getEyePosition(1.0F));
                trader.level().playSound(null, trader, SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.5F);
                trader.level().getEntities(EntityType.TRADER_LLAMA, trader.getBoundingBox().inflate(Config.WANDERING_TRADER.challenge.maxDetectionDistance.get()), entity -> true).forEach(llama -> llama.setTarget(openingPlayer));
                ((ServerLevel) trader.level()).sendParticles(ParticleTypes.ANGRY_VILLAGER, trader.getX(), trader.getEyeY(), trader.getZ(), 1, 0, 0, 0, 0);
                data.addDislikedPlayer(openingPlayer, trader.level().getGameTime());
                return;
            }

            if(generateBackpackLoot(trader, data))
            {
                /*UnlockManager.getTracker(openingPlayer).flatMap(tracker -> tracker.getProgressTracker(WanderingBagBackpack.ID)).ifPresent(tracker -> {
                    ((WanderingBagBackpack.PickpocketProgressTracker) tracker).addTrader(trader, openingPlayer);
                });*/
            }
            Services.BACKPACK.openBackpackScreen(openingPlayer, trader.getInventory(), trader.getId(), 0, 8, 1, false, UnlockableSlots.ALL, Pagination.NONE, Augments.EMPTY, WANDERING_BAG_TRANSLATION, UnlockableSlots.NONE);
            openingPlayer.level().playSound(openingPlayer, trader.getX(), trader.getY() + 1.0, trader.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 0.15F, 1.0F);
        });
    }

    private static boolean generateBackpackLoot(WanderingTrader trader, TraderPickpocketing data)
    {
        if(!data.isLootSpawned())
        {
            int size = trader.getInventory().getContainerSize();
            int reserved = size / 4; // Maybe eventually a config option
            int count = trader.level().random.nextInt(Math.max(reserved, 1)) + (size - reserved);
            List<Integer> randomSlotIndexes = IntStream.range(0, size).boxed().collect(Collectors.toCollection(ArrayList::new));
            Collections.shuffle(randomSlotIndexes);
            MerchantOffers offers = trader.getOffers();
            for(int i = 0; i < size; i++)
            {
                if(!Config.WANDERING_TRADER.challenge.generateEmeraldsOnly.get() && i < count)
                {
                    MerchantOffer offer = offers.get(trader.level().random.nextInt(offers.size()));
                    ItemStack loot = offer.getResult().copy();
                    loot.setCount(Mth.clamp(loot.getCount() * (trader.level().random.nextInt(Config.WANDERING_TRADER.challenge.maxLootMultiplier.get()) + 1), 0, loot.getMaxStackSize()));
                    trader.getInventory().setItem(randomSlotIndexes.get(i), loot);
                }
                else
                {
                    ItemStack stack = new ItemStack(Items.EMERALD, trader.level().random.nextInt(Config.WANDERING_TRADER.challenge.maxEmeraldStack.get()) + 1);
                    trader.getInventory().setItem(randomSlotIndexes.get(i), stack);
                }
            }
            data.setLootSpawned();
            return true;
        }
        return false;
    }

    private static void patchTraderAiGoals(WanderingTrader trader)
    {
        GoalSelector selector = Services.ENTITY.getGoalSelector(trader);
        trader.removeAllGoals(goal -> goal instanceof LookAtPlayerGoal);
        selector.addGoal(2, new LootAtDetectedPlayerGoal(trader));
        selector.addGoal(9, new PickpocketLookAtPlayerGoal(trader, Player.class, 3.0F, 1.0F));
    }

    private static double getMaxDetectionDistance()
    {
        return Config.WANDERING_TRADER.challenge.maxDetectionDistance.get();
    }

    private static class PickpocketLookAtPlayerGoal extends LookAtPlayerGoal
    {
        public PickpocketLookAtPlayerGoal(Mob entity, Class<? extends LivingEntity> entityClass, float distance, float probability)
        {
            super(entity, entityClass, distance, probability);
        }

        @Override
        public boolean canUse()
        {
            if(TraderPickpocketing.get(this.mob).map(TraderPickpocketing::isBackpackEquipped).orElse(false))
            {
                return false;
            }
            return super.canUse();
        }
    }

    private static class LootAtDetectedPlayerGoal extends LookAtPlayerGoal
    {
        private final WanderingTrader trader;

        public LootAtDetectedPlayerGoal(WanderingTrader trader)
        {
            super(trader, Player.class, Config.WANDERING_TRADER.challenge.maxDetectionDistance.get().floatValue() * 2.0F, 1.0F);
            this.trader = trader;
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse()
        {
            super.canUse();
            if(this.trader.getLastHurtByMob() == null && this.lookAt instanceof Player)
            {
                TraderPickpocketing data = TraderPickpocketing.get(this.trader).orElse(null);
                return data != null && data.isBackpackEquipped() && data.getDetectedPlayers().containsKey((Player) this.lookAt);
            }
            return false;
        }

        @Override
        public boolean canContinueToUse()
        {
            if(this.lookAt instanceof Player && this.lookAt.distanceTo(this.trader) <= Config.WANDERING_TRADER.challenge.maxDetectionDistance.get().floatValue() * 2.0)
            {
                TraderPickpocketing data = TraderPickpocketing.get(this.trader).orElse(null);
                return data != null && data.getDetectedPlayers().containsKey((Player) this.lookAt);
            }
            return false;
        }

        @Override
        public void start()
        {
            if(this.trader.level() instanceof ServerLevel serverLevel && this.lookAt instanceof Player player)
            {
                if(TraderPickpocketing.get(this.trader).map(data -> data.isDislikedPlayer(player)).orElse(false))
                {
                    serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, this.trader.getX(), this.trader.getEyeY(), this.trader.getZ(), 1, 0, 0, 0, 0);
                    serverLevel.playSound(null, this.trader, SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.5F);
                }
            }
        }

        @Override
        public void tick()
        {
            if(this.lookAt instanceof Player && isPlayerSeenByLivingEntity(this.trader, (Player) this.lookAt, Config.WANDERING_TRADER.challenge.maxDetectionDistance.get() * 2))
            {
                this.trader.getLookControl().setLookAt(this.lookAt.getX(), this.lookAt.getEyeY(), this.lookAt.getZ());
            }
        }
    }
}
