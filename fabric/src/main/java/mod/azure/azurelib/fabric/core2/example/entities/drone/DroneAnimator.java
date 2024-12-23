package mod.azure.azurelib.fabric.core2.example.entities.drone;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;

public class DroneAnimator extends AzEntityAnimator<Drone> {

    private static final ResourceLocation ANIMATIONS = AzureLib.modResource("animations/entity/drone.animation.json");

    public DroneAnimator() {
        super();
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Drone> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME)
                .setTransitionLength(5)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Drone animatable) {
        return ANIMATIONS;
    }

}
