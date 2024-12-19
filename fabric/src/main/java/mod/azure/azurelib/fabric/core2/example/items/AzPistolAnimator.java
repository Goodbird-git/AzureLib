package mod.azure.azurelib.fabric.core2.example.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.AzAnimatorConfig;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.core2.animation.impl.AzItemAnimator;
import mod.azure.azurelib.core2.animation.primitive.AzLoopType;
import mod.azure.azurelib.core2.animation.primitive.AzRawAnimation;

public class AzPistolAnimator extends AzItemAnimator {

    private static final ResourceLocation ANIMATIONS = AzureLib.modResource("animations/item/pistol.animation.json");

    private static final String FIRING_ANIMATION_NAME = "firing";

    private static final AzRawAnimation FIRING_ANIMATION = AzRawAnimation.begin()
        .then(FIRING_ANIMATION_NAME, AzLoopType.PLAY_ONCE);

    public AzPistolAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<ItemStack> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, "base_controller")
                .triggerableAnim(FIRING_ANIMATION_NAME, FIRING_ANIMATION)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(ItemStack animatable) {
        return ANIMATIONS;
    }

}
