package com.mrcrayfish.backpacked.client;

import com.mrcrayfish.backpacked.client.renderer.backpack.function.DrawModelFunction;
import com.mrcrayfish.backpacked.client.renderer.backpack.function.FunctionTypes;
import com.mrcrayfish.backpacked.client.renderer.backpack.function.RotateFunction;
import com.mrcrayfish.backpacked.client.renderer.backpack.function.ScaleFunction;
import com.mrcrayfish.backpacked.client.renderer.backpack.function.StackFunction;
import com.mrcrayfish.backpacked.client.renderer.backpack.function.TranslateFunction;
import com.mrcrayfish.backpacked.client.renderer.backpack.value.source.AnimationTickSource;
import com.mrcrayfish.backpacked.client.renderer.backpack.value.source.SourceTypes;
import com.mrcrayfish.backpacked.client.renderer.backpack.value.source.StaticSource;
import com.mrcrayfish.backpacked.client.renderer.backpack.value.source.TickCountSource;
import com.mrcrayfish.backpacked.client.renderer.backpack.value.source.WalkPositionSource;
import com.mrcrayfish.backpacked.client.renderer.backpack.value.source.WalkSpeedSource;
import com.mrcrayfish.backpacked.client.renderer.backpack.value.source.WaveformSource;
import com.mrcrayfish.framework.api.event.InputEvents;

/**
 * Author: MrCrayfish
 */
public class ClientBootstrap
{
    public static void earlyInit()
    {
        InputEvents.REGISTER_KEY_MAPPING.register(consumer -> consumer.accept(Keys.KEY_BACKPACK));
        FunctionTypes.register(StackFunction.TYPE);
        FunctionTypes.register(TranslateFunction.TYPE);
        FunctionTypes.register(RotateFunction.TYPE);
        FunctionTypes.register(ScaleFunction.TYPE);
        FunctionTypes.register(DrawModelFunction.TYPE);
        SourceTypes.register(StaticSource.TYPE);
        SourceTypes.register(TickCountSource.TYPE);
        SourceTypes.register(WalkPositionSource.TYPE);
        SourceTypes.register(WalkSpeedSource.TYPE);
        SourceTypes.register(WaveformSource.TYPE);
        SourceTypes.register(AnimationTickSource.TYPE);
    }

    public static void init()
    {
        ClientEvents.init();
    }
}
