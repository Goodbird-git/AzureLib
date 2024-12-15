package mod.azure.azurelib.fabric.core2.example;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.AzAnimatorConfig;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.core2.animation.primitive.AzRawAnimation;

public class DroneAnimator extends AzEntityAnimator<Drone> {

    private static final ResourceLocation ANIMATIONS = AzureLib.modResource("animations/entity/drone.animation.json");

    private static final String IDLE_ANIMATION_NAME = "animation.idle";

    private static final String WALK_ANIMATION_NAME = "animation.walk";

    private static final AzRawAnimation IDLE_ANIMATION = AzRawAnimation.begin().thenLoop(IDLE_ANIMATION_NAME);

    private static final AzRawAnimation WALK_ANIMATION = AzRawAnimation.begin().thenLoop(WALK_ANIMATION_NAME);

    public DroneAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Drone> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, "base_controller")
                .setTransitionLength(5)
                .triggerableAnim(IDLE_ANIMATION_NAME, IDLE_ANIMATION)
                .triggerableAnim(WALK_ANIMATION_NAME, WALK_ANIMATION)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Drone drone) {
        return ANIMATIONS;
    }
}
