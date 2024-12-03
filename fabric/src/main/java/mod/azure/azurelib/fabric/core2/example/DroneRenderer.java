package mod.azure.azurelib.fabric.core2.example;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.core2.render.entity.AzEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DroneRenderer extends AzEntityRenderer<Drone> {

    private static final ResourceLocation MODEL = AzureLib.modResource("geo/entity/drone.geo.json");
    private static final ResourceLocation TEXTURE = AzureLib.modResource("textures/entity/drone.png");

    public DroneRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected @Nullable AzEntityAnimator<Drone> createAnimator() {
        return new DroneAnimator();
    }

    @Override
    protected @NotNull ResourceLocation getModelLocation(Drone drone) {
        return MODEL;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Drone drone) {
        return TEXTURE;
    }
}
