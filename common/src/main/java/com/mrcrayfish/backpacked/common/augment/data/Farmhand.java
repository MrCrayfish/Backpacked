package com.mrcrayfish.backpacked.common.augment.data;

import com.google.common.base.Preconditions;
import com.mrcrayfish.backpacked.common.PlaceSoundControls;
import com.mrcrayfish.backpacked.common.UseItemOnBlockFaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Farmhand extends SavedData
{
    public static final String ID = "backpacked_farmhand";
    public static final int PLANT_TIME = 14;
    private static final int MAX_DISTANCE_SQR = 16 * 16;

    private final ServerLevel level;
    private final Map<BlockPos, DelayedPlantTask> tasks = new HashMap<>();

    public Farmhand(ServerLevel level)
    {
        this.level = level;
    }

    public boolean plant(ItemStack stack, BlockPos pos, ServerPlayer player)
    {
        Preconditions.checkArgument(stack.getItem() instanceof BlockItem);

        // Player must be in the same level
        if(player.level() != this.level)
            return false;

        // Player must be alive and near the planting position
        if(!player.isAlive() || pos.distToCenterSqr(player.position()) > MAX_DISTANCE_SQR)
            return false;

        if(!this.tasks.containsKey(pos))
        {
            this.tasks.put(pos, new DelayedPlantTask(stack, pos, player.getUUID()));
            this.setDirty();
            return true;
        }
        return false;
    }

    public boolean isPlanting(BlockPos pos)
    {
        return this.tasks.containsKey(pos);
    }

    public void tick()
    {
        this.tasks.entrySet().removeIf(entry -> {
            DelayedPlantTask task = entry.getValue();
            task.delay--;
            if(task.delay <= 0) {
                task.run(this.level);
                this.setDirty();
                return true;
            }
            return false;
        });
    }

    public static Farmhand load(ServerLevel level, CompoundTag tag)
    {
        Farmhand farmhand = new Farmhand(level);
        ListTag tasks = tag.getList("Tasks", CompoundTag.TAG_COMPOUND);
        tasks.forEach(tag1 -> {
            try {
                CompoundTag taskTag = (CompoundTag) tag1;
                var task = DelayedPlantTask.load(taskTag);
                farmhand.tasks.put(task.pos, task);
            } catch(Exception ignored) {}
        });
        return farmhand;
    }

    @Override
    public CompoundTag save(CompoundTag tag)
    {
        ListTag tasks = new ListTag();
        this.tasks.forEach((pos, task) -> {
            tasks.add(task.save());
        });
        tag.put("Tasks", tasks);
        return tag;
    }

    private static final class DelayedPlantTask
    {
        private final ItemStack stack;
        private final BlockPos pos;
        private final UUID submitter;
        private int delay = PLANT_TIME;

        private DelayedPlantTask(ItemStack stack, BlockPos pos, UUID submitter)
        {
            this.stack = stack;
            this.pos = pos;
            this.submitter = submitter;
        }

        public void run(ServerLevel level)
        {
            if(this.stack.getItem() instanceof BlockItem item)
            {
                ServerPlayer player = level.getServer().getPlayerList().getPlayer(this.submitter);
                if(player != null && player.level() == level && player.isAlive() && this.pos.distToCenterSqr(player.position()) <= MAX_DISTANCE_SQR)
                {
                    InteractionResult result = PlaceSoundControls.runWithOptions(false, true, () -> {
                        return item.useOn(UseItemOnBlockFaceContext.create(level, player, this.stack, this.pos.below(), Direction.UP));
                    });
                    if(result.consumesAction())
                    {
                        BlockState state = level.getBlockState(this.pos.below());
                        Vec3 particle = this.pos.getCenter().subtract(0, 0.5, 0);
                        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), particle.x, particle.y, particle.z, 10, 0.3F, 0.3F, 0.3F, 0.15F);
                        return;
                    }
                }
            }
            // Fallback if fails
            Vec3 spawn = this.pos.getCenter().add(0, 0.5, 0);
            level.addFreshEntity(new ItemEntity(level, spawn.x, spawn.y, spawn.z, this.stack.copy()));
        }

        public static DelayedPlantTask load(CompoundTag tag)
        {
            ItemStack stack = ItemStack.of(tag.getCompound("Stack"));
            BlockPos pos = BlockPos.of(tag.getLong("Pos"));
            UUID submitter = tag.getUUID("Submitter");
            return new DelayedPlantTask(stack, pos, submitter);
        }

        public CompoundTag save()
        {
            CompoundTag tag = new CompoundTag();
            tag.put("Stack", this.stack.save(new CompoundTag()));
            tag.putLong("Pos", this.pos.asLong());
            tag.putUUID("Submitter", this.submitter);
            return tag;
        }
    }

    public interface Access
    {
        Farmhand backpacked$getFarmhand();
    }
}
