package mod.azure.azurelib.fabric.core2.example.azure;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.core2.animation.primitive.AzRawAnimation;

public class DoomHunterAnimator extends AzEntityAnimator<DoomHunter> {

    private static final ResourceLocation ANIMATIONS = AzureLib.modResource(
        "animations/entity/doomhunter.animation.json"
    );

    private static final String IDLE_ANIMATION_NAME = "idle";

    private static final String MELEE_ANIMATION_NAME = "chainsaw";

    private static final AzRawAnimation IDLE_ANIMATION = AzRawAnimation.begin().thenLoop(IDLE_ANIMATION_NAME);

    private static final AzRawAnimation MELEE_ANIMATION = AzRawAnimation.begin().thenLoop(MELEE_ANIMATION_NAME);

    @Override
    public void registerControllers(AzAnimationControllerContainer<DoomHunter> animationControllerContainer) {
        animationControllerContainer.add(
            new AzAnimationController<>(this, "base_controller", 0)
                .triggerableAnim(IDLE_ANIMATION_NAME, IDLE_ANIMATION)
                .triggerableAnim(MELEE_ANIMATION_NAME, MELEE_ANIMATION)
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(DoomHunter drone) {
        return ANIMATIONS;
    }
}
