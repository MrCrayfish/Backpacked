package com.mrcrayfish.backpacked.client.renderer.backpack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.client.renderer.BackpackRenderer;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Author: MrCrayfish
 */
public record BackpackRenderContext(PoseStack pose, MultiBufferSource source, int light, ItemStack stack,
                                    Backpack backpack, @Nullable LivingEntity entity, float partialTick, int animationTick, BackpackRenderer renderer)
{

}
