package com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrcrayfish.backpacked.client.renderer.backpack.BackpackRenderContext;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value.Value;
import com.mrcrayfish.backpacked.util.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SpawnParticleFunction implements BaseFunction
{
    private static final Set<Integer> SPAWNED = new HashSet<>();

    public static final Type TYPE = new Type(
        Utils.rl("spawn_particle"),
        RecordCodecBuilder.<SpawnParticleFunction>mapCodec(builder -> builder.group(
            ParticleTypes.CODEC.fieldOf("particle").forGetter(o -> o.particle),
            Vector.CODEC.optionalFieldOf("position", Vector.ZERO).forGetter(o -> o.position),
            Vector.CODEC.optionalFieldOf("motion", Vector.ZERO).forGetter(o -> o.motion)
        ).apply(builder, SpawnParticleFunction::new))
    );

    private final ParticleOptions particle;
    private final Vector position;
    private final Vector motion;

    public SpawnParticleFunction(ParticleOptions particle, Vector position, Vector motion)
    {
        this.particle = particle;
        this.position = position;
        this.motion = motion;
    }

    @Override
    public Type type()
    {
        return TYPE;
    }

    @Override
    public void apply(BackpackRenderContext context)
    {
        // Don't spawn particles in customisation menu
        if(context.scene().isCustomisationMenu())
            return;

        if(!context.renderMode().canShowEffects())
            return;

        if(Minecraft.getInstance().isPaused())
            return;

        Level level = context.level();
        if(level == null)
            return;

        // Create a key to keep track if particle has already been spawned
        int key = Objects.hash(context.scene(), context.entity(), this);
        if(SPAWNED.contains(key))
            return;

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vector3f pos = context.pose().last().pose().getTranslation(new Vector3f());
        pos = camera.getPosition().toVector3f().add(pos);

        Vector3d offset = new Vector3d();
        offset.x = this.position.x.get(context);
        offset.y = this.position.y.get(context);
        offset.z = this.position.z.get(context);
        offset.rotate(context.pose().last().pose().getNormalizedRotation(new Quaterniond()));

        Vector3d motion = new Vector3d();
        motion.x = this.motion.x.get(context);
        motion.y = this.motion.y.get(context);
        motion.z = this.motion.z.get(context);
        motion.rotate(context.pose().last().pose().getNormalizedRotation(new Quaterniond()));

        double particleX = pos.x + offset.x;
        double particleY = pos.y + offset.y;
        double particleZ = pos.z + offset.z;
        level.addParticle(this.particle, particleX, particleY, particleZ, motion.x, motion.y, motion.z);

        SPAWNED.add(key);
    }

    public static void clearSpawned()
    {
        SPAWNED.clear();
    }

    private record Vector(Value x, Value y, Value z)
    {
        private static final Vector ZERO = new Vector(Value.ZERO, Value.ZERO, Value.ZERO);
        private static final Codec<Vector> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Value.EITHER_CODEC.optionalFieldOf("x", Value.ZERO).forGetter(o -> o.x),
            Value.EITHER_CODEC.optionalFieldOf("y", Value.ZERO).forGetter(o -> o.y),
            Value.EITHER_CODEC.optionalFieldOf("z", Value.ZERO).forGetter(o -> o.z)
        ).apply(builder, Vector::new));
    }
}
