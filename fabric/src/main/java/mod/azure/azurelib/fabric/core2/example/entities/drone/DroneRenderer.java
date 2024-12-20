package mod.azure.azurelib.fabric.core2.example.entities.drone;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.render.entity.AzEntityRenderer;
import mod.azure.azurelib.core2.render.entity.AzEntityRendererConfig;

public class DroneRenderer extends AzEntityRenderer<Drone> {

    private static final ResourceLocation MODEL = AzureLib.modResource("geo/entity/drone.geo.json");

    private static final ResourceLocation TEXTURE = AzureLib.modResource("textures/entity/drone.png");

    public DroneRenderer(EntityRendererProvider.Context context) {
        super(
            AzEntityRendererConfig.<Drone>builder(MODEL, TEXTURE).setAnimatorProvider(DroneAnimator::new).build(),
            context
        );
    }
}
