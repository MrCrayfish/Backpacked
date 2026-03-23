package com.mrcrayfish.backpacked.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;
import com.mrcrayfish.backpacked.common.augment.data.Farmhand;
import com.mrcrayfish.backpacked.common.augment.data.Recall;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.SavedDataStorage;
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
public abstract class ServerLevelMixin implements Farmhand.Access, Recall.Access
{
    @Unique
    private Farmhand backpacked$farmhand;

    @Unique
    private Recall backpacked$recall;

    @Override
    public Farmhand backpacked$getFarmhand()
    {
        return this.backpacked$farmhand;
    }

    @Override
    public Recall backpacked$getRecall()
    {
        return this.backpacked$recall;
    }

    @Shadow
    public abstract SavedDataStorage getDataStorage();

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void backpacked$init(MinecraftServer server, Executor executor, LevelStorageSource.LevelStorageAccess levelStorage, ServerLevelData levelData, ResourceKey<Level> dimension, LevelStem levelStem, boolean isDebug, long biomeZoomSeed, List<CustomSpawner> customSpawners, boolean tickTime, CallbackInfo ci)
    {
        ServerLevel level = (ServerLevel) (Object) this;
        this.backpacked$farmhand = this.getDataStorage().computeIfAbsent(Farmhand.TYPE);
        this.backpacked$recall = this.getDataStorage().computeIfAbsent(Recall.TYPE);
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void backpacked$tickTail(BooleanSupplier unknown, CallbackInfo ci, @Local(ordinal = 0) ProfilerFiller profiler)
    {
        ServerLevel level = (ServerLevel) (Object) this;
        profiler.push("backpacked_farmhand");
        this.backpacked$farmhand.tick(level);
        profiler.popPush("backpacked_recall");
        this.backpacked$recall.tick(level);
        profiler.pop();
    }
}
