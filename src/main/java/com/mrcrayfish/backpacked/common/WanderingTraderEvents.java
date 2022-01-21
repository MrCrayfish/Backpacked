package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.backpack.WanderingBagBackpack;
import com.mrcrayfish.backpacked.common.data.PickpocketChallenge;
import com.mrcrayfish.backpacked.core.ModItems;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncVillagerBackpack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookAtWithoutMovingGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.PacketDistributor;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Author: MrCrayfish
 */
public class WanderingTraderEvents
{
    private static final Field goalsField = ObfuscationReflectionHelper.findField(GoalSelector.class, "field_220892_d");

    public static void onConstructWanderingTrader(WanderingTraderEntity trader)
    {
        if(!trader.level.isClientSide())
        {
            if(trader.level.random.nextInt(Config.COMMON.wanderingTraderBackpackChance.get()) == 0)
            {
                PickpocketChallenge.get(trader).ifPresent(data -> data.setBackpackEquipped(true));
            }
            patchTraderAiGoals(trader);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event)
    {
        if(event.getTarget().getType() != EntityType.WANDERING_TRADER)
            return;

        WanderingTraderEntity trader = (WanderingTraderEntity) event.getTarget();
        PickpocketChallenge.get(trader).ifPresent(data ->
        {
            if(data.isBackpackEquipped())
            {
                Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new MessageSyncVillagerBackpack(event.getTarget().getId()));
            }
        });
    }

    @SubscribeEvent
    public void onTickLivingEntity(LivingEvent.LivingUpdateEvent event)
    {
        LivingEntity entity = event.getEntityLiving();
        World world = entity.level;
        if(world.isClientSide() || entity.getType() != EntityType.WANDERING_TRADER)
            return;

        PickpocketChallenge.get(entity).ifPresent(data ->
        {
            if(!data.isBackpackEquipped())
                return;
            Map<PlayerEntity, Long> detectedPlayers = data.getDetectedPlayers();
            WanderingTraderEntity trader = (WanderingTraderEntity) entity;
            List<PlayerEntity> newDetectedPlayers = this.findDetectedPlayers(trader);
            newDetectedPlayers.forEach(player -> detectedPlayers.put(player, world.getGameTime()));
            detectedPlayers.entrySet().removeIf(this.createForgetPlayerPredicate(trader, world));
        });
    }

    private List<PlayerEntity> findDetectedPlayers(LivingEntity entity)
    {
        return entity.level.getEntities(EntityType.PLAYER, entity.getBoundingBox().inflate(getMaxDetectionDistance()), player -> {
            return isPlayerInLivingEntityVision(entity, player) && isPlayerSeenByLivingEntity(entity, player, Config.COMMON.wanderingTraderMaxDetectionDistance.get()) || !player.isCrouching() && isPlayerMoving(player);
        });
    }

    private Predicate<Map.Entry<PlayerEntity, Long>> createForgetPlayerPredicate(WanderingTraderEntity trader, World world)
    {
        return entry -> !entry.getKey().isAlive() || entry.getKey().distanceTo(trader) > Config.COMMON.wanderingTraderMaxDetectionDistance.get() * 2.0 || (world.getGameTime() - entry.getValue() > Config.COMMON.wanderingTraderForgetTime.get() && entry.getKey().distanceTo(trader) >= Config.COMMON.wanderingTraderMaxDetectionDistance.get());
    }

    // Determines if the player is in the living entities vision
    private static boolean isPlayerInLivingEntityVision(LivingEntity entity, PlayerEntity player)
    {
        Vector3d between = entity.position().subtract(player.position());
        float angle = (float) Math.toDegrees(MathHelper.atan2(between.z, between.x)) - 90F;
        return MathHelper.degreesDifferenceAbs(entity.yHeadRot + 180F, angle) <= 90F;
    }

    private static boolean isPlayerSeenByLivingEntity(LivingEntity entity, PlayerEntity player, double distance)
    {
        if(entity.level != player.level || entity.distanceTo(player) > distance)
            return false;

        Vector3d livingEyePos = new Vector3d(entity.getX(), entity.getEyeY(), entity.getZ());
        Vector3d playerEyePos = new Vector3d(player.getX(), player.getEyeY(), player.getZ());
        if(performRayTrace(livingEyePos, playerEyePos, entity).getType() == RayTraceResult.Type.MISS)
            return true;

        Vector3d playerLegPos = new Vector3d(player.getX(), player.getY() + 0.5, player.getZ());
        return performRayTrace(livingEyePos, playerLegPos, entity).getType() == RayTraceResult.Type.MISS;
    }

    private static RayTraceResult performRayTrace(Vector3d start, Vector3d end, Entity source)
    {
        return source.level.clip(new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, source));
    }

    private static boolean isPlayerMoving(PlayerEntity player)
    {
        return ((IMovedAccess) player).backpackedMoved();
    }

    @SuppressWarnings("unchecked")
    private static void patchTraderAiGoals(WanderingTraderEntity trader)
    {
        try
        {
            Set<PrioritizedGoal> goals = (Set<PrioritizedGoal>) goalsField.get(trader.goalSelector);
            if(goals != null)
            {
                goals.removeIf(goal -> goal.getGoal() instanceof LookAtWithoutMovingGoal);
            }
            trader.goalSelector.addGoal(2, new LootAtDetectedPlayerGoal(trader));
            trader.goalSelector.addGoal(9, new PickpocketLookAtWithoutMovingGoal(trader, PlayerEntity.class, 3.0F, 1.0F));
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

    private static class PickpocketLookAtWithoutMovingGoal extends LookAtWithoutMovingGoal
    {
        public PickpocketLookAtWithoutMovingGoal(MobEntity entity, Class<? extends LivingEntity> entityClass, float distance, float probability)
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

    private static class LootAtDetectedPlayerGoal extends LookAtGoal
    {
        private final WanderingTraderEntity trader;

        public LootAtDetectedPlayerGoal(WanderingTraderEntity trader)
        {
            super(trader, PlayerEntity.class, Config.COMMON.wanderingTraderMaxDetectionDistance.get().floatValue() * 2.0F, 1.0F);
            this.trader = trader;
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse()
        {
            super.canUse();
            if(this.lookAt instanceof PlayerEntity)
            {
                PickpocketChallenge data = PickpocketChallenge.get(this.trader).orElse(null);
                return data != null && data.isBackpackEquipped() && data.getDetectedPlayers().containsKey((PlayerEntity) this.lookAt);
            }
            return false;
        }

        @Override
        public boolean canContinueToUse()
        {
            if(this.lookAt instanceof PlayerEntity && this.lookAt.distanceTo(this.trader) <= Config.COMMON.wanderingTraderMaxDetectionDistance.get().floatValue() * 2.0)
            {
                PickpocketChallenge data = PickpocketChallenge.get(this.trader).orElse(null);
                return data != null && data.getDetectedPlayers().containsKey((PlayerEntity) this.lookAt);
            }
            return false;
        }

        @Override
        public void start()
        {
            if(this.trader.level instanceof ServerWorld)
            {
                ((ServerWorld) this.trader.level).sendParticles(ParticleTypes.ANGRY_VILLAGER, this.trader.getX(), this.trader.getEyeY() + 0.5, this.trader.getZ(), 1, 0, 0, 0, 0);
                this.trader.level.playSound(null, this.trader, SoundEvents.VILLAGER_NO, SoundCategory.NEUTRAL, 1.0F, 1.5F);
            }
        }

        @Override
        public void tick()
        {
            if(isPlayerSeenByLivingEntity(this.trader, (PlayerEntity) this.lookAt, Config.COMMON.wanderingTraderMaxDetectionDistance.get() * 2))
            {
                this.trader.getLookControl().setLookAt(this.lookAt.getX(), this.lookAt.getEyeY(), this.lookAt.getZ());
            }
        }
    }
}
