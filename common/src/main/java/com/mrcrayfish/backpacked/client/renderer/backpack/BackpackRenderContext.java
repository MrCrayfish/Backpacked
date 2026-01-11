package com.mrcrayfish.backpacked.client.renderer.backpack;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public record BackpackRenderContext(
        Scene scene,
        RenderMode renderMode,
        PoseStack pose,
        Identifier baseModel,
        Identifier strapsModel,
        @Nullable LivingEntityDataState entityData,
        @Nullable LevelDataState levelData,
        int entityId,
        int light,
        int tickCount,
        float partialTick,
        Consumer<BlockModelPart> modelRenderer
){}
