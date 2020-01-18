package com.mrcrayfish.backpacked.asm;

import com.mrcrayfish.backpacked.Backpacked;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
@IFMLLoadingPlugin.TransformerExclusions({"com.mrcrayfish.backpacked.asm"})
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("Backpacked")
@IFMLLoadingPlugin.SortingIndex(value = 1001)
public class BackpackedPlugin implements IFMLLoadingPlugin
{
    public static File LOCATION = null;

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] {"com.mrcrayfish.backpacked.asm.BackpackedTransformer"};
    }

    @Override
    public String getModContainerClass()
    {
        return Backpacked.class.getName();
    }

    @Nullable
    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {
        if((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"))
        {
            try
            {
                LOCATION = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            }
            catch(URISyntaxException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            LOCATION = (File) data.get("coremodLocation");
        }
    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
