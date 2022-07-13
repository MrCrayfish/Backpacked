package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.WanderingBagBackpack;
import com.mrcrayfish.backpacked.common.data.PickpocketChallenge;
import com.mrcrayfish.backpacked.inventory.container.BackpackContainerMenu;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncVillagerBackpack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
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
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Author: MrCrayfish
 */
public class WanderingTraderEvents
{
    private static final Field goalsField = ObfuscationReflectionHelper.findField(GoalSelector.class, "f_25345_");
    public static final Component WANDERING_BAG_TRANSLATION = Component.translatable("backpacked.backpack.wandering_bag");

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinLevelEvent event)
    {
        if(!event.getLevel().isClientSide() && event.getEntity() instanceof WanderingTrader trader && Config.COMMON.spawnBackpackOnWanderingTraders.get())
        {
            PickpocketChallenge.get(trader).ifPresent(data ->
            {
                if(!data.isInitialized())
                {
                    boolean equipped = trader.level.random.nextInt(Config.COMMON.wanderingTraderBackpackChance.get()) == 0;
                    data.setBackpackEquipped(equipped);
                    data.setInitialized();
                }
            });
            patchTraderAiGoals(trader);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        if(event.getTarget().getType() != EntityType.WANDERING_TRADER)
            return;

        WanderingTrader trader = (WanderingTrader) event.getTarget();
        PickpocketChallenge.get(trader).ifPresent(data ->
        {
            if(data.isBackpackEquipped())
            {
                Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new MessageSyncVillagerBackpack(event.getTarget().getId()));
            }
        });
    }

    @SubscribeEvent
    public void onTickLivingEntity(LivingEvent.LivingTickEvent event)
    {
        LivingEntity entity = event.getEntity();
        Level level = entity.level;
        if(level.isClientSide() || entity.getType() != EntityType.WANDERING_TRADER)
            return;

        WanderingTrader trader = (WanderingTrader) entity;
        if(trader.getUnhappyCounter() > 0)
        {
            trader.setUnhappyCounter(trader.getUnhappyCounter() - 1);
        }

        PickpocketChallenge.get(entity).ifPresent(data ->
        {
            if(!data.isBackpackEquipped())
                return;
            Map<Player, Long> detectedPlayers = data.getDetectedPlayers();
            List<Player> newDetectedPlayers = this.findDetectedPlayers(trader);
            newDetectedPlayers.forEach(player -> detectedPlayers.put(player, level.getGameTime()));
            detectedPlayers.entrySet().removeIf(this.createForgetPlayerPredicate(trader, level));
            data.getDislikedPlayers().entrySet().removeIf(entry -> level.getGameTime() - entry.getValue() > Config.COMMON.dislikeCooldown.get());
        });
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event)
    {
        Entity entity = event.getTarget();
        if(!entity.level.isClientSide() && entity instanceof WanderingTrader trader)
        {
            if(!Config.COMMON.dislikedPlayersCanTrade.get() && PickpocketChallenge.get(trader).map(data -> data.isBackpackEquipped() && data.isDislikedPlayer(event.getEntity())).orElse(false))
            {
                trader.setUnhappyCounter(20);
                trader.level.playSound(null, trader, SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.5F);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    private List<Player> findDetectedPlayers(LivingEntity entity)
    {
        return entity.level.getEntities(EntityType.PLAYER, entity.getBoundingBox().inflate(getMaxDetectionDistance()), player -> {
            return isPlayerInLivingEntityVision(entity, player) && isPlayerSeenByLivingEntity(entity, player, Config.COMMON.wanderingTraderMaxDetectionDistance.get()) || !player.isCrouching() && isPlayerMoving(player);
        });
    }

    private Predicate<Map.Entry<Player, Long>> createForgetPlayerPredicate(WanderingTrader trader, Level world)
    {
        return entry -> !entry.getKey().isAlive() || entry.getKey().distanceTo(trader) > Config.COMMON.wanderingTraderMaxDetectionDistance.get() * 2.0 || (world.getGameTime() - entry.getValue() > Config.COMMON.wanderingTraderForgetTime.get() && entry.getKey().distanceTo(trader) >= Config.COMMON.wanderingTraderMaxDetectionDistance.get());
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

        if(entity.level != player.level || entity.distanceTo(player) > distance)
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
        return player.hasEffect(MobEffects.INVISIBILITY) && player.getArmorCoverPercentage() <= 0 && StreamSupport.stream(player.getHandSlots().spliterator(), false).allMatch(ItemStack::isEmpty) && Backpacked.getBackpackStack(player).isEmpty();
    }

    private static BlockHitResult performRayTrace(Vec3 start, Vec3 end, Entity source)
    {
        return source.level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, source));
    }

    private static boolean isPlayerMoving(Player player)
    {
        return ((IMovedAccess) player).backpackedMoved();
    }

    public static void openBackpack(WanderingTrader trader, ServerPlayer openingPlayer)
    {
        PickpocketChallenge.get(trader).ifPresent(data ->
        {
            if(!data.isBackpackEquipped())
                return;

            if(data.getDetectedPlayers().containsKey(openingPlayer))
            {
                trader.setUnhappyCounter(20);
                trader.getLookControl().setLookAt(openingPlayer.getEyePosition(1.0F));
                trader.level.playSound(null, trader, SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.5F);
                trader.level.getEntities(EntityType.TRADER_LLAMA, trader.getBoundingBox().inflate(Config.COMMON.wanderingTraderMaxDetectionDistance.get()), entity -> true).forEach(llama -> llama.setTarget(openingPlayer));
                ((ServerLevel) trader.level).sendParticles(ParticleTypes.ANGRY_VILLAGER, trader.getX(), trader.getEyeY(), trader.getZ(), 1, 0, 0, 0, 0);
                data.addDislikedPlayer(openingPlayer, trader.level.getGameTime());
                return;
            }

            if(generateBackpackLoot(trader, data))
            {
                UnlockTracker.get(openingPlayer).ifPresent(unlockTracker ->
                {
                    unlockTracker.getProgressTracker(WanderingBagBackpack.ID).ifPresent(tracker ->
                    {
                        ((WanderingBagBackpack.PickpocketProgressTracker) tracker).addTrader(trader, openingPlayer);
                    });
                });
            }

            NetworkHooks.openScreen(openingPlayer, new SimpleMenuProvider((id, playerInventory, entity1) -> {
                return new BackpackContainerMenu(id, entity1.getInventory(), trader.getInventory(), 8, 1, false);
            }, WANDERING_BAG_TRANSLATION), buffer -> {
                buffer.writeVarInt(8);
                buffer.writeVarInt(1);
                buffer.writeBoolean(false);
            });
            openingPlayer.level.playSound(openingPlayer, trader.getX(), trader.getY() + 1.0, trader.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 0.15F, 1.0F);
        });
    }

    private static boolean generateBackpackLoot(WanderingTrader trader, PickpocketChallenge data)
    {
        if(!data.isLootSpawned())
        {
            int size = trader.getInventory().getContainerSize();
            int reserved = size / 4; // Maybe eventually a config option
            int count = trader.level.random.nextInt(Math.max(reserved, 1)) + (size - reserved);
            List<Integer> randomSlotIndexes = IntStream.range(0, size).boxed().collect(Collectors.toCollection(ArrayList::new));
            Collections.shuffle(randomSlotIndexes);
            MerchantOffers offers = trader.getOffers();
            for(int i = 0; i < size; i++)
            {
                if(!Config.COMMON.generateEmeraldsOnly.get() && i < count)
                {
                    MerchantOffer offer = offers.get(trader.level.random.nextInt(offers.size()));
                    ItemStack loot = offer.getResult().copy();
                    loot.setCount(Mth.clamp(loot.getCount() * (trader.level.random.nextInt(Config.COMMON.maxLootMultiplier.get()) + 1), 0, loot.getMaxStackSize()));
                    trader.getInventory().setItem(randomSlotIndexes.get(i), loot);
                }
                else
                {
                    ItemStack stack = new ItemStack(Items.EMERALD, trader.level.random.nextInt(Config.COMMON.maxEmeraldStack.get()) + 1);
                    trader.getInventory().setItem(randomSlotIndexes.get(i), stack);
                }
            }
            data.setLootSpawned();
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static void patchTraderAiGoals(WanderingTrader trader)
    {
        try
        {
            Set<WrappedGoal> goals = (Set<WrappedGoal>) goalsField.get(trader.goalSelector);
            if(goals != null)
            {
                goals.removeIf(goal -> goal.getGoal() instanceof LookAtPlayerGoal);
            }
            trader.goalSelector.addGoal(2, new LootAtDetectedPlayerGoal(trader));
            trader.goalSelector.addGoal(9, new PickpocketLookAtPlayerGoal(trader, Player.class, 3.0F, 1.0F));
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private static double getMaxDetectionDistance()
    {
        return Config.COMMON.wanderingTraderMaxDetectionDistance.get();
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
            if(PickpocketChallenge.get(this.mob).map(PickpocketChallenge::isBackpackEquipped).orElse(false))
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
            super(trader, Player.class, Config.COMMON.wanderingTraderMaxDetectionDistance.get().floatValue() * 2.0F, 1.0F);
            this.trader = trader;
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse()
        {
            super.canUse();
            if(this.trader.getLastHurtByMob() == null && this.lookAt instanceof Player)
            {
                PickpocketChallenge data = PickpocketChallenge.get(this.trader).orElse(null);
                return data != null && data.isBackpackEquipped() && data.getDetectedPlayers().containsKey((Player) this.lookAt);
            }
            return false;
        }

        @Override
        public boolean canContinueToUse()
        {
            if(this.lookAt instanceof Player && this.lookAt.distanceTo(this.trader) <= Config.COMMON.wanderingTraderMaxDetectionDistance.get().floatValue() * 2.0)
            {
                PickpocketChallenge data = PickpocketChallenge.get(this.trader).orElse(null);
                return data != null && data.getDetectedPlayers().containsKey((Player) this.lookAt);
            }
            return false;
        }

        @Override
        public void start()
        {
            if(this.trader.level instanceof ServerLevel)
            {
                if(PickpocketChallenge.get(this.trader).map(data -> data.isDislikedPlayer((Player) this.lookAt)).orElse(false))
                {
                    ((ServerLevel) this.trader.level).sendParticles(ParticleTypes.ANGRY_VILLAGER, this.trader.getX(), this.trader.getEyeY(), this.trader.getZ(), 1, 0, 0, 0, 0);
                    this.trader.level.playSound(null, this.trader, SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.5F);
                }
            }
        }

        @Override
        public void tick()
        {
            if(isPlayerSeenByLivingEntity(this.trader, (Player) this.lookAt, Config.COMMON.wanderingTraderMaxDetectionDistance.get() * 2))
            {
                this.trader.getLookControl().setLookAt(this.lookAt.getX(), this.lookAt.getEyeY(), this.lookAt.getZ());
            }
        }
    }
}
