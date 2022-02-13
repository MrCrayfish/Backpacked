package com.mrcrayfish.backpacked.common;

import com.mrcrayfish.backpacked.Config;
import com.mrcrayfish.backpacked.common.data.PickpocketChallenge;
import com.mrcrayfish.backpacked.network.Network;
import com.mrcrayfish.backpacked.network.message.MessageSyncVillagerBackpack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.PacketDistributor;

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
    private static final Field goalsField = ObfuscationReflectionHelper.findField(GoalSelector.class, "f_25345_");

    public static void onConstructWanderingTrader(WanderingTrader trader)
    {
        if(!trader.level.isClientSide())
        {
            if(trader.level.random.nextInt(Config.COMMON.wanderingTraderBackpackChance.get()) == 0)
            {
                PickpocketChallenge.get(trader).ifPresent(data -> data.setBackpackEquipped(true));
                patchTraderAiGoals(trader);
            }
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
                Network.getPlayChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getPlayer()), new MessageSyncVillagerBackpack(event.getTarget().getId()));
            }
        });
    }

    @SubscribeEvent
    public void onTickLivingEntity(LivingEvent.LivingUpdateEvent event)
    {
        LivingEntity entity = event.getEntityLiving();
        Level level = entity.level;
        if(level.isClientSide() || entity.getType() != EntityType.WANDERING_TRADER)
            return;

        PickpocketChallenge.get(entity).ifPresent(data ->
        {
            Map<Player, Long> detectedPlayers = data.getDetectedPlayers();
            WanderingTrader trader = (WanderingTrader) entity;
            List<Player> newDetectedPlayers = this.findDetectedPlayers(trader);
            newDetectedPlayers.forEach(player -> detectedPlayers.put(player, level.getGameTime()));
            detectedPlayers.entrySet().removeIf(this.createForgetPlayerPredicate(trader, level));
        });
    }

    private List<Player> findDetectedPlayers(LivingEntity entity)
    {
        return entity.level.getEntities(EntityType.PLAYER, entity.getBoundingBox().inflate(8.0), player -> {
            return isPlayerInLivingEntityVision(entity, player) && isPlayerSeenByLivingEntity(entity, player) || !player.isCrouching() && isPlayerMoving(player);
        });
    }

    private Predicate<Map.Entry<Player, Long>> createForgetPlayerPredicate(WanderingTrader trader, Level world)
    {
        return entry -> !entry.getKey().isAlive() || entry.getKey().distanceTo(trader) > 16.0F || world.getGameTime() - entry.getValue() > 600;
    }

    // Determines if the player is in the living entities vision
    private static boolean isPlayerInLivingEntityVision(LivingEntity entity, Player player)
    {
        Vec3 between = entity.position().subtract(player.position());
        float angle = (float) Math.toDegrees(Mth.atan2(between.z, between.x)) - 90F;
        return Mth.degreesDifferenceAbs(entity.yHeadRot + 180F, angle) <= 90F;
    }

    private static boolean isPlayerSeenByLivingEntity(LivingEntity entity, Player player)
    {
        if(entity.level != player.level || entity.distanceTo(player) > 10.0)
            return false;

        Vec3 livingEyePos = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
        Vec3 playerEyePos = new Vec3(player.getX(), player.getEyeY(), player.getZ());
        if(performRayTrace(livingEyePos, playerEyePos, entity).getType() == BlockHitResult.Type.MISS)
            return true;

        Vec3 playerLegPos = new Vec3(player.getX(), player.getY() + 0.5, player.getZ());
        return performRayTrace(livingEyePos, playerLegPos, entity).getType() == BlockHitResult.Type.MISS;
    }

    private static BlockHitResult performRayTrace(Vec3 start, Vec3 end, Entity source)
    {
        return source.level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, source));
    }

    private static boolean isPlayerMoving(Player player)
    {
        return ((IMovedAccess) player).backpackedMoved();
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
            super(trader, Player.class, 8.0F, 1.0F);
            this.trader = trader;
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse()
        {
            super.canUse();
            if(this.lookAt instanceof Player)
            {
                PickpocketChallenge data = PickpocketChallenge.get(this.trader).orElse(null);
                return data != null && data.isBackpackEquipped() && data.getDetectedPlayers().containsKey((Player) this.lookAt);
            }
            return false;
        }

        @Override
        public boolean canContinueToUse()
        {
            if(this.lookAt instanceof Player && this.lookAt.distanceTo(this.trader) <= 8.0)
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
                ((ServerLevel) this.trader.level).sendParticles(ParticleTypes.ANGRY_VILLAGER, this.trader.getX(), this.trader.getEyeY() + 0.5, this.trader.getZ(), 1, 0, 0, 0, 0);
                this.trader.level.playSound(null, this.trader, SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1.0F, 1.5F);
            }
        }

        @Override
        public void tick()
        {
            if(isPlayerSeenByLivingEntity(this.trader, (Player) this.lookAt))
            {
                this.trader.getLookControl().setLookAt(this.lookAt.getX(), this.lookAt.getEyeY(), this.lookAt.getZ());
            }
        }
    }
}
