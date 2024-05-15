package mod.azure.azurelib;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AzureLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonListener {

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(AzureLibMod.GEO_EXAMPLE_ENTITY.get(), MobEntity.createMobAttributes()
                .add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MAX_HEALTH, 1.0D).build());
    }
}
