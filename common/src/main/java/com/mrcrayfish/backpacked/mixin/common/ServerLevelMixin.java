package com.mrcrayfish.backpacked.mixin.common;

import com.mrcrayfish.backpacked.common.Farmhand;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements Farmhand.Access
{
    @Unique
    private Farmhand backpacked$farmhand;

    @Override
    public Farmhand backpacked$getFarmhand()
    {
        return this.backpacked$farmhand;
    }

    @Shadow
    public abstract DimensionDataStorage getDataStorage();

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void backpacked$init(MinecraftServer server, Executor executor, LevelStorageSource.LevelStorageAccess access, ServerLevelData data, ResourceKey key, LevelStem stem, ChunkProgressListener listener, boolean bool1, long long1, List list1, boolean bool2, RandomSequences sequences, CallbackInfo ci)
    {
        this.backpacked$farmhand = this.getDataStorage().computeIfAbsent(Farmhand.factory((ServerLevel) (Object) this), Farmhand.ID);
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void backpacked$tickTail(BooleanSupplier unknown, CallbackInfo ci)
    {
        ServerLevel level = (ServerLevel) (Object) this;
        ProfilerFiller profiler = level.getProfiler();
        profiler.push("backpacked_farmhand");
        this.backpacked$farmhand.tick();
        profiler.pop();
    }
}
