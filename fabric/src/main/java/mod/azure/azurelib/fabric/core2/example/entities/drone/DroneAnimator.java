package mod.azure.azurelib.fabric.core2.example.entities.drone;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.AzAnimatorConfig;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;

public class DroneAnimator extends AzEntityAnimator<Drone> {

    private static final ResourceLocation ANIMATIONS = AzureLib.modResource("animations/entity/drone.animation.json");

    public DroneAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Drone> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, DroneAnimationRefs.FULL_BODY_CONTROLLER_NAME)
                .setTransitionLength(5)
                .triggerableAnim(
                    DroneAnimationRefs.ATTACK_CLAW_ANIMATION_NAME,
                    DroneAnimationRefs.ATTACK_CLAW_ANIMATION
                )
                .triggerableAnim(
                    DroneAnimationRefs.ATTACK_TAIL_ANIMATION_NAME,
                    DroneAnimationRefs.ATTACK_TAIL_ANIMATION
                )
                .triggerableAnim(DroneAnimationRefs.CRAWL_ANIMATION_NAME, DroneAnimationRefs.CRAWL_ANIMATION)
                .triggerableAnim(DroneAnimationRefs.CRAWL_HOLD_ANIMATION_NAME, DroneAnimationRefs.CRAWL_HOLD_ANIMATION)
                .triggerableAnim(DroneAnimationRefs.IDLE_ANIMATION_NAME, DroneAnimationRefs.IDLE_ANIMATION)
                .triggerableAnim(DroneAnimationRefs.RUN_ANIMATION_NAME, DroneAnimationRefs.RUN_ANIMATION)
                .triggerableAnim(DroneAnimationRefs.SWIM_ANIMATION_NAME, DroneAnimationRefs.SWIM_ANIMATION)
                .triggerableAnim(DroneAnimationRefs.WALK_ANIMATION_NAME, DroneAnimationRefs.WALK_ANIMATION)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Drone animatable) {
        return ANIMATIONS;
    }

}
