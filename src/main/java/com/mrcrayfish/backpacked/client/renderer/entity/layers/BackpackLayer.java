package com.mrcrayfish.backpacked.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrcrayfish.backpacked.Backpacked;
import com.mrcrayfish.backpacked.client.ClientHandler;
import com.mrcrayfish.backpacked.client.model.BackpackModel;
import com.mrcrayfish.backpacked.common.BackpackModelProperty;
import com.mrcrayfish.backpacked.integration.Curios;
import com.mrcrayfish.backpacked.item.BackpackItem;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class BackpackLayer<T extends Player, M extends PlayerModel<T>> extends RenderLayer<T, M>
{
    private static final Map<String, Supplier<BackpackModel>> VARIANTS = new HashMap<>();
    private static final Supplier<BackpackModel> DEFAULT_SUPPLIER = () -> ClientHandler.getModelInstances().getStandardModel();

    public BackpackLayer(RenderLayerParent<T, M> renderer)
    {
        super(renderer);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource renderTypeBuffer, int p_225628_3_, T player, float p_225628_5_, float p_225628_6_, float partialTick, float p_225628_8_, float p_225628_9_, float p_225628_10_)
    {
        ItemStack backpack = Backpacked.getBackpackStack(player);
        if(backpack.getItem() instanceof BackpackItem backpackItem)
        {
            ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
            if(chestStack.getItem() == Items.ELYTRA && !canRenderWithElytra(backpack))
                return;

            if(Backpacked.isCuriosLoaded() && !Curios.isBackpackVisible(player))
                return;

            String modelName = backpack.getOrCreateTag().getString("BackpackModel");
            BackpackModel model = VARIANTS.getOrDefault(modelName, DEFAULT_SUPPLIER).get();
            if(model == null)
                return;

            stack.pushPose();
            model.transformToPlayerBody(this.getParentModel().body, !chestStack.isEmpty());
            model.setupAngles(player, player.tickCount, partialTick);
            VertexConsumer builder = ItemRenderer.getFoilBuffer(renderTypeBuffer, model.renderType(model.getTextureLocation()), false, backpack.hasFoil());
            model.renderToBuffer(stack, builder, p_225628_3_, OverlayTexture.NO_OVERLAY, 1.0F, 2.0F, 2.0F, 2.0F);
            stack.popPose();
        }
    }

    public static boolean canRenderWithElytra(ItemStack stack)
    {
        return stack.getOrCreateTag().getBoolean(BackpackModelProperty.SHOW_WITH_ELYTRA.getTagName());
    }

    public static boolean canShowEnchantmentGlint(ItemStack stack)
    {
        return stack.getOrCreateTag().getBoolean(BackpackModelProperty.SHOW_GLINT.getTagName());
    }

    public synchronized static void registerModel(ResourceLocation id, Supplier<BackpackModel> model)
    {
        VARIANTS.putIfAbsent(id.toString(), model);
    }

    public static Supplier<BackpackModel> getModel(String id)
    {
        return VARIANTS.getOrDefault(id, DEFAULT_SUPPLIER);
    }
}
