package mod.azure.azurelib.fabric.core2.example.blocks;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.AzAnimatorConfig;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.core2.animation.impl.AzBlockAnimator;
import mod.azure.azurelib.core2.animation.primitive.AzRawAnimation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class StargateBlockEntityAnimator extends AzBlockAnimator<StargateBlockEntity> {

    private static final ResourceLocation ANIMATIONS = AzureLib.modResource(
            "animations/block/stargate.animation.json"
    );

    private static final String SPIN_ANIMATION_NAME = "spinning";

    private static final AzRawAnimation SPIN_ANIMATION = AzRawAnimation.begin().thenLoop(SPIN_ANIMATION_NAME);

    protected StargateBlockEntityAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<StargateBlockEntity> animationControllerContainer) {
        animationControllerContainer.add(
                AzAnimationController.builder(this, "base_controller")
                        .triggerableAnim(SPIN_ANIMATION_NAME, SPIN_ANIMATION)
                        .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(StargateBlockEntity animatable) {
        return ANIMATIONS;
    }
}
