package mod.azure.azurelib.fabric.core2.example.armors;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.core2.animation.impl.AzItemAnimator;
import mod.azure.azurelib.core2.animation.primitive.AzLoopType;
import mod.azure.azurelib.core2.animation.primitive.AzRawAnimation;

public class AzDoomArmorAnimator extends AzItemAnimator {

    private static final ResourceLocation ANIMATIONS = AzureLib.modResource(
        "animations/item/doomicorn.animation.json"
    );

    private static final String EQUIP_ANIMATION_NAME = "equipping";

    private static final AzRawAnimation EQUIP_ANIMATION = AzRawAnimation.begin()
        .then(EQUIP_ANIMATION_NAME, AzLoopType.PLAY_ONCE);

    @Override
    public void registerControllers(AzAnimationControllerContainer<ItemStack> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, "base_controller")
                .triggerableAnim(EQUIP_ANIMATION_NAME, EQUIP_ANIMATION)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(ItemStack animatable) {
        return ANIMATIONS;
    }
}
