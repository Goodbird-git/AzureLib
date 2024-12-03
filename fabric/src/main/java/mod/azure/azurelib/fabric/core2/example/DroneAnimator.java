package mod.azure.azurelib.fabric.core2.example;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.core2.animation.AzAnimationState;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.core2.animation.primitive.AzRawAnimation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DroneAnimator extends AzEntityAnimator<Drone> {

    private static final ResourceLocation ANIMATIONS = AzureLib.modResource("animations/entity/drone.animation.json");

    private static final String IDLE_ANIMATION_NAME = "animation.idle";
    private static final AzRawAnimation IDLE_ANIMATION = AzRawAnimation.begin().thenLoop(IDLE_ANIMATION_NAME);

    @Override
    public void registerControllers(AzAnimationControllerContainer<Drone> animationControllerContainer) {
        animationControllerContainer.add(
            new AzAnimationController<>(this, "base_controller", 0, this::handle)
        );
    }
    public PlayState handle(AzAnimationState<Drone> event) {
        return event.setAndContinue(IDLE_ANIMATION);
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Drone drone) {
        return ANIMATIONS;
    }
}
