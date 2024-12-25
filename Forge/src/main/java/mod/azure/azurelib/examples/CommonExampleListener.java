package mod.azure.azurelib.examples;

import mod.azure.azurelib.AzureLib;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

public class CommonExampleListener {

    @SubscribeEvent
    public void onRegisterEntities(RegistryEvent.Register<EntityEntry> event) {
        int id = 0;
        event.getRegistry().register(EntityEntryBuilder.create().entity(DoomHunter.class).name("Doom Hunter")
                .id(AzureLib.modResource("doomhunter"), id++).tracker(160, 2, false).build());
    }
}
