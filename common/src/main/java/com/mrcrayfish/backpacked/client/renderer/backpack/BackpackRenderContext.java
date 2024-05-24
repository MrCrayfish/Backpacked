package com.mrcrayfish.backpacked.client.renderer.backpack;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.backpacked.common.backpack.Backpack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Author: MrCrayfish
 */
public record BackpackRenderContext(PoseStack pose, MultiBufferSource source,
                                    ItemRenderer itemRenderer, int light, ItemStack stack,
                                    Backpack backpack, LivingEntity entity, float partialTick)
{

}
