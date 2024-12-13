package mod.azure.azurelib.fabric.core2.example;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.AzAnimatorConfig;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.core2.animation.primitive.AzRawAnimation;

public class FacehuggerAnimator extends AzEntityAnimator<Facehugger> {

    private static final ResourceLocation ANIMATIONS = AzureLib.modResource(
        "animations/entity/facehugger.animation.json"
    );

    private static final String IDLE_ANIMATION_NAME = "animation.run";

    private static final AzRawAnimation IDLE_ANIMATION = AzRawAnimation.begin().thenLoop(IDLE_ANIMATION_NAME);

    public FacehuggerAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<Facehugger> animationControllerContainer) {
        animationControllerContainer.add(
            new AzAnimationController<>(this, "base_controller", 0)
                .triggerableAnim(IDLE_ANIMATION_NAME, IDLE_ANIMATION)
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(Facehugger facehugger) {
        return ANIMATIONS;
    }
}
