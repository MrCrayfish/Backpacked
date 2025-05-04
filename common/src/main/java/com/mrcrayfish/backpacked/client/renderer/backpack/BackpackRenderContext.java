package com.mrcrayfish.backpacked.client.renderer.backpack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.client.backpack.ClientBackpack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public record BackpackRenderContext(PoseStack pose, MultiBufferSource source, int light, ItemStack stack,
                                    ClientBackpack backpack, @Nullable LivingEntity entity, @Nullable Level level, float partialTick, Consumer<BakedModel> bakedModelRenderer, ItemRenderer itemRenderer)
{

}
