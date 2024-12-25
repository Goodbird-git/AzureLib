package mod.azure.azurelib;

import mod.azure.azurelib.examples.DoomHunter;
import mod.azure.azurelib.examples.DoomHunterRenderer;
import mod.azure.azurelib.network.AzureLibNetwork;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = AzureLib.MOD_ID, name = AzureLib.NAME, version = AzureLib.VERSION)
public class AzureLibMod {

    public AzureLibMod() {
        AzureLib.initialize();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        AzureLib.LOGGER = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        AzureLib.initialize();
        if (event.getSide().isClient()) {
            AzureLibNetwork.initClient();
        } else {
            AzureLibNetwork.initServer();
        }
    }

    @SubscribeEvent
    public void onRegisterEntities(RegistryEvent.Register<EntityEntry> event) {
        int id = 0;
        event.getRegistry().register(EntityEntryBuilder.create().entity(DoomHunter.class).name("Doom Hunter")
                .id(AzureLib.modResource("doomhunter"), id++).tracker(160, 2, false).build());
    }

    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    public void registerRenderers(FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(DoomHunter.class, DoomHunterRenderer::new);
    }
}
