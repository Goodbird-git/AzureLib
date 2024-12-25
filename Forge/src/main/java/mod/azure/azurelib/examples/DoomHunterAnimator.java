package mod.azure.azurelib.examples;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.animation.impl.AzEntityAnimator;
import net.minecraft.util.ResourceLocation;

public class DoomHunterAnimator extends AzEntityAnimator<DoomHunter> {

    private static final ResourceLocation ANIMATIONS = AzureLib.modResource(
        "animations/entity/doomhunter.animation.json"
    );

    public DoomHunterAnimator() {
        super();
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<DoomHunter> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, "base_controller")
                .build()
        );
    }

    @Override
    public ResourceLocation getAnimationLocation(DoomHunter drone) {
        return ANIMATIONS;
    }
}
