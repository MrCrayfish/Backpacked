package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.client.augment.AugmentSettingsFactories;
import com.mrcrayfish.backpacked.client.augment.AugmentSettingsMenu;
import com.mrcrayfish.backpacked.client.augment.widget.FunnellingMenu;
import com.mrcrayfish.backpacked.client.gui.screen.layout.PaddedLinearLayout;
import com.mrcrayfish.backpacked.client.gui.screen.widget.CustomButton;
import com.mrcrayfish.backpacked.client.renderer.backpack.DefaultRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.RendererTypes;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.AdvancedRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.condition.*;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function.*;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value.*;
import com.mrcrayfish.backpacked.common.augment.impl.FunnellingAugment;
import com.mrcrayfish.backpacked.common.augment.impl.GiantAugment;
import com.mrcrayfish.framework.api.event.InputEvents;
import net.minecraft.network.chat.Component;

/**
 * Author: MrCrayfish
 */
public class ClientBootstrap
{
    public static void earlyInit()
    {
        InputEvents.REGISTER_KEY_MAPPING.register(consumer -> consumer.accept(Keys.KEY_BACKPACK));
        RendererTypes.register(DefaultRenderer.TYPE);
        RendererTypes.register(AdvancedRenderer.TYPE);
        FunctionTypes.register(PushMatrixFunction.TYPE);
        FunctionTypes.register(TranslateMatrixFunction.TYPE);
        FunctionTypes.register(RotateMatrixFunction.TYPE);
        FunctionTypes.register(ScaleMatrixFunction.TYPE);
        FunctionTypes.register(DrawModelFunction.TYPE);
        FunctionTypes.register(SpawnParticleFunction.TYPE);
        FunctionTypes.register(ConditionalFunction.TYPE);
        ValueTypes.register(ConstantValue.TYPE);
        ValueTypes.register(TickCountValue.TYPE);
        ValueTypes.register(WaveformValue.TYPE);
        ValueTypes.register(AndValue.TYPE);
        ValueTypes.register(ExpressionValue.TYPE);
        ValueTypes.register(EntityDataValue.TYPE);
        ValueTypes.register(LevelDataValue.TYPE);
        ConditionTypes.register(AndCondition.TYPE);
        ConditionTypes.register(ManyCondition.TYPE);
        ConditionTypes.register(InvertedCondition.TYPE);
        ConditionTypes.register(TestValueCondition.TYPE);
    }

    public static void init()
    {
        ClientEvents.init();
        AugmentSettingsFactories.registerFactory(GiantAugment.TYPE, (handler, supplier, updater) -> {
            return new AugmentSettingsMenu(handler, menu -> {
                PaddedLinearLayout layout = PaddedLinearLayout.horizontal().padding(2);
                layout.addChild(CustomButton.builder().setSize(16, 16).setMessage(Component.literal("1")).setAction(customButton -> {
                    GiantAugment augment = supplier.get();
                    updater.accept(augment.setSize(1));
                    menu.hide();
                }).build());
                return layout;
            });
        });
        AugmentSettingsFactories.registerFactory(FunnellingAugment.TYPE, FunnellingMenu::new);
    }
}
