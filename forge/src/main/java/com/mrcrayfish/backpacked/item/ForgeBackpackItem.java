package com.mrcrayfish.backpacked.item;

import com.mrcrayfish.backpacked.client.BackpackClientItemExtensions;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class ForgeBackpackItem extends BackpackItem
{
    public ForgeBackpackItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer)
    {
        consumer.accept(new BackpackClientItemExtensions());
    }
}
