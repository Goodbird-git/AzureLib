package mod.azure.azurelib.fabric.core2.example.entities.doomhunter;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull ResourceLocation getAnimationLocation(DoomHunter drone) {
        return ANIMATIONS;
    }
}
