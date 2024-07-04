package mod.azure.azurelib;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = AzureLib.MOD_ID, name = AzureLib.NAME, version = AzureLib.VERSION)
public class AzureLibMod {

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        AzureLib.LOGGER = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        AzureLib.LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}
