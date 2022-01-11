package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.client.ModelInstances;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.BackpackModelProperty;
import com.mrcrayfish.backpacked.integration.Curios;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class BackpackLayer<T extends PlayerEntity, M extends BipedModel<T>> extends LayerRenderer<T, M>
{
    private static final Map<String, BackpackModel> VARIANTS = new HashMap<>();

    public BackpackLayer(IEntityRenderer<T, M> renderer)
    {
        super(renderer);
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, int p_225628_3_, T player, float p_225628_5_, float p_225628_6_, float partialTick, float p_225628_8_, float p_225628_9_, float p_225628_10_)
    {
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(backpack.getItem() instanceof BackpackItem)
        {
            ItemStack chestStack = player.getItemBySlot(EquipmentSlotType.CHEST);
            if(chestStack.getItem() == Items.ELYTRA && !canRenderWithElytra(backpack))
                return;

            if(Backpacked.isCuriosLoaded() && !Curios.isBackpackVisible(player))
                return;

            stack.pushPose();
            BackpackItem backpackItem = (BackpackItem) backpack.getItem();
            String modelName = backpack.getOrCreateTag().getString("BackpackModel");
            BackpackModel model = VARIANTS.getOrDefault(modelName, backpackItem.getDefaultModel());
            model.setupAngles(player, this.getParentModel().body, !chestStack.isEmpty(), partialTick);
            IVertexBuilder builder = ItemRenderer.getFoilBuffer(renderTypeBuffer, model.renderType(model.getTextureLocation()), false, backpack.hasFoil());
            model.renderToBuffer(stack, builder, p_225628_3_, OverlayTexture.NO_OVERLAY, 1.0F, 2.0F, 2.0F, 2.0F);
            stack.popPose();
        }
    }

    public static boolean canRenderWithElytra(ItemStack stack)
    {
        return stack.getOrCreateTag().getBoolean(BackpackModelProperty.SHOW_WITH_ELYTRA.getTagName());
    }

    public synchronized static <T extends BackpackModel> void registerModel(ResourceLocation id, T model)
    {
        VARIANTS.putIfAbsent(id.toString(), model);
    }

    public static BackpackModel getModel(String id)
    {
        return VARIANTS.getOrDefault(id, ModelInstances.STANDARD);
    }

    public static Map<String, BackpackModel> getBackpackModels()
    {
        return ImmutableMap.copyOf(VARIANTS);
    }
}
