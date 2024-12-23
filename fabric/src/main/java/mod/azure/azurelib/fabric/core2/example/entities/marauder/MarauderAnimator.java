package mod.azure.azurelib.fabric.core2.example.entities.marauder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzKeyframeCallbacks;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;

public class MarauderAnimator extends AzEntityAnimator<MarauderEntity> {

    private static final ResourceLocation ANIMATIONS = AzureLib.modResource(
        "animations/entity/marauder.animation.json"
    );

    public MarauderAnimator() {
        super();
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<MarauderEntity> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, "base_controller")
                .setTransitionLength(5)
                .setKeyframeCallbacks(
                    AzKeyframeCallbacks.<MarauderEntity>builder()
                        .setSoundKeyframeHandler(
                            event -> {
                                if (event.getKeyframeData().getSound().equals("walk")) {
                                    event.getAnimatable()
                                        .level()
                                        .playLocalSound(
                                            event.getAnimatable().getX(),
                                            event.getAnimatable().getY(),
                                            event.getAnimatable().getZ(),
                                            SoundEvents.METAL_STEP,
                                            SoundSource.HOSTILE,
                                            1.00F,
                                            1.0F,
                                            true
                                        );
                                }
                                if (event.getKeyframeData().getSound().equals("run")) {
                                    event.getAnimatable()
                                        .level()
                                        .playLocalSound(
                                            event.getAnimatable().getX(),
                                            event.getAnimatable().getY(),
                                            event.getAnimatable().getZ(),
                                            SoundEvents.SKELETON_STEP,
                                            SoundSource.HOSTILE,
                                            1.00F,
                                            1.0F,
                                            true
                                        );
                                }
                                if (event.getKeyframeData().getSound().equals("portal")) {
                                    event.getAnimatable()
                                        .level()
                                        .playLocalSound(
                                            event.getAnimatable().getX(),
                                            event.getAnimatable().getY(),
                                            event.getAnimatable().getZ(),
                                            SoundEvents.PORTAL_AMBIENT,
                                            SoundSource.HOSTILE,
                                            0.20F,
                                            1.0F,
                                            true
                                        );
                                }
                                if (event.getKeyframeData().getSound().equals("axe")) {
                                    event.getAnimatable()
                                        .level()
                                        .playLocalSound(
                                            event.getAnimatable().getX(),
                                            event.getAnimatable().getY(),
                                            event.getAnimatable().getZ(),
                                            SoundEvents.ENDER_EYE_LAUNCH,
                                            SoundSource.HOSTILE,
                                            1.00F,
                                            1.0F,
                                            true
                                        );
                                }
                            }
                        )
                        .build()
                )
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(MarauderEntity drone) {
        return ANIMATIONS;
    }
}
