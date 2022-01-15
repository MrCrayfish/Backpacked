package com.mrcrayfish.backpacked.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public abstract class BackpackModel extends Model
{
    protected final ResourceLocation texture;
    protected final ModelPart backpack;
    protected final ModelPart bag;
    protected final ModelPart strap;

    public BackpackModel(ModelPart root, ResourceLocation texture)
    {
        this(root, texture, RenderType::entityCutoutNoCull);
    }

    public BackpackModel(ModelPart root, ResourceLocation texture, Function<ResourceLocation, RenderType> renderType)
    {
        super(renderType);
        this.texture = texture;
        this.backpack = root.getChild("backpack");
        this.bag = this.backpack.getChild("bag");
        this.strap = this.bag.getChild("strap");
    }

    public void setupAngles(Player player, ModelPart body, boolean armour, float partialTick)
    {
        this.setupAngles(body, armour);
    }

    @Deprecated
    public void setupAngles(ModelPart body, boolean armour)
    {
        this.beforeRender(body, armour);
    }

    private void beforeRender(ModelPart body, boolean armour)
    {
        ModelPart root = this.getRoot();
        root.copyFrom(body);

        ModelPart bag = this.getBag();
        bag.setPos(0.0F, -0.2F, 2.0F + (armour ? 1.0F : 0.0F));

        ModelPart straps = this.getStraps();
        straps.visible = !armour;
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer builder, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_)
    {
        matrixStack.scale(1.05F, 1.05F, 1.05F);
        this.getRoot().render(matrixStack, builder, p_225598_3_, p_225598_4_);
    }

    public void tickForPlayer(Vec3 pos, Player player)
    {
    }

    protected ModelPart getRoot()
    {
        return this.backpack;
    }

    public ModelPart getBag()
    {
        return this.bag;
    }

    public ModelPart getStraps()
    {
        return this.strap;
    }

    public ResourceLocation getTextureLocation()
    {
        return this.texture;
    }
}
