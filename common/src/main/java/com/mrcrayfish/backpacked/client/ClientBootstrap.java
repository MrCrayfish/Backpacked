package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.AdvancedRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.DefaultRenderer;
import com.mrcrayfish.backpacked.client.renderer.backpack.RendererTypes;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function.DrawModelFunction;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function.FunctionTypes;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function.RotateFunction;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function.ScaleFunction;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function.StackFunction;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.function.TranslateFunction;
import com.mrcrayfish.backpacked.client.renderer.backpack.advanced.value.*;
import com.mrcrayfish.framework.api.event.InputEvents;

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
        FunctionTypes.register(StackFunction.TYPE);
        FunctionTypes.register(TranslateFunction.TYPE);
        FunctionTypes.register(RotateFunction.TYPE);
        FunctionTypes.register(ScaleFunction.TYPE);
        FunctionTypes.register(DrawModelFunction.TYPE);
        ValueTypes.register(ConstantValue.TYPE);
        ValueTypes.register(TickCountValue.TYPE);
        ValueTypes.register(WalkPositionValue.TYPE);
        ValueTypes.register(WalkSpeedValue.TYPE);
        ValueTypes.register(WaveformValue.TYPE);
        ValueTypes.register(AnimationTickValue.TYPE);
        ValueTypes.register(AndValue.TYPE);
        ValueTypes.register(ExpressionValue.TYPE);
    }

    public static void init()
    {
        ClientEvents.init();
    }
}
