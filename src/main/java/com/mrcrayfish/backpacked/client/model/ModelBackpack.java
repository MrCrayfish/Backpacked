package com.mrcrayfish.backpacked.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

/**
 * Author: MrCrayfish
 */
public class ModelBackpack<T extends LivingEntity> extends BipedModel<T>
{
    public ModelRenderer backpack;

    public ModelBackpack()
    {
        super(0.0F);
        this.texWidth = 64;
        this.texHeight = 32;
        this.backpack = new ModelRenderer(this, 0, 0);
        this.backpack.setPos(0.0F, 0.0F, 0.0F);
        this.backpack.addBox(-3.5F, 1.0F, 2.0F, 7, 9, 4, 0.0F);
    }

    @Override
    protected Iterable<ModelRenderer> headParts()
    {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelRenderer> bodyParts()
    {
        return ImmutableList.of(this.backpack);
    }

    public void setupAngles(BipedModel<T> model)
    {
        copyProperties(model.body, this.backpack);
    }

    private static void copyProperties(ModelRenderer source, ModelRenderer target)
    {
        target.copyFrom(source);
    }
}
