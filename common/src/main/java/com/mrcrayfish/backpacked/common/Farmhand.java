package com.mrcrayfish.backpacked.common;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public final class Farmhand extends SavedData
{
    public static final String ID = "backpacked_farmhand";
    public static final int PLANT_TIME = 10;

    private final ServerLevel level;
    private final Map<BlockPos, DelayedPlantTask> tasks = new HashMap<>();

    @SuppressWarnings("DataFlowIssue")
    public static SavedData.Factory<Farmhand> factory(ServerLevel level)
    {
        return new SavedData.Factory<>(() -> new Farmhand(level), (tag, provider) -> load(level, provider, tag), null);
    }

    public Farmhand(ServerLevel level)
    {
        this.level = level;
    }

    public boolean plant(ItemStack stack, BlockPos pos)
    {
        Preconditions.checkArgument(stack.getItem() instanceof BlockItem);
        if(!this.tasks.containsKey(pos))
        {
            this.tasks.put(pos, new DelayedPlantTask(stack, pos));
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
            if(--task.delay <= 0) {
                task.run(this.level);
                this.setDirty();
                return true;
            }
            return false;
        });
    }

    private static Farmhand load(ServerLevel level, HolderLookup.Provider provider, CompoundTag tag)
    {
        Farmhand farmhand = new Farmhand(level);
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        ListTag tasks = tag.getList("Tasks", CompoundTag.TAG_COMPOUND);
        tasks.forEach(tag1 -> {
            DelayedPlantTask.CODEC.parse(ops, tag1).result().ifPresent(task -> {
                farmhand.tasks.put(task.pos, task);
            });
        });
        return farmhand;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider)
    {
        RegistryOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
        ListTag tasks = new ListTag();
        this.tasks.forEach((pos, task) -> {
            DelayedPlantTask.CODEC.encodeStart(ops, task).result().ifPresent(tasks::add);
        });
        tag.put("Tasks", tasks);
        return tag;
    }

    private static final class DelayedPlantTask
    {
        private static final Codec<DelayedPlantTask> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.OPTIONAL_CODEC.fieldOf("stack").forGetter(task -> task.stack),
            BlockPos.CODEC.fieldOf("pos").forGetter(task -> task.pos)
        ).apply(instance, DelayedPlantTask::new));

        private final ItemStack stack;
        private final BlockPos pos;
        private int delay = PLANT_TIME;

        private DelayedPlantTask(ItemStack stack, BlockPos pos)
        {
            this.stack = stack;
            this.pos = pos;
        }

        public void run(ServerLevel level)
        {
            if(this.stack.getItem() instanceof BlockItem item)
            {
                InteractionResult result = item.place(new BlockPlaceContext(UseItemOnBlockFaceContext.create(level, this.stack, this.pos, Direction.UP)));
                if(result.consumesAction())
                {
                    BlockState state = level.getBlockState(this.pos.below());
                    Vec3 particle = this.pos.getBottomCenter();
                    level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), particle.x, particle.y, particle.z, 10, 0.3F, 0.3F, 0.3F, 0.15F);
                    return;
                }
            }
            // Fallback if fails
            Vec3 spawn = this.pos.getBottomCenter().add(0, 1, 0);
            level.addFreshEntity(new ItemEntity(level, spawn.x, spawn.y, spawn.z, this.stack.copy()));
        }
    }

    public interface Access
    {
        Farmhand backpacked$getFarmhand();
    }
}
