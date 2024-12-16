package mod.azure.azurelib.fabric.core2.example.entities.marauder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.NotNull;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.AzAnimatorConfig;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzKeyFrameCallbacks;
import mod.azure.azurelib.core2.animation.impl.AzEntityAnimator;
import mod.azure.azurelib.core2.animation.primitive.AzLoopType;
import mod.azure.azurelib.core2.animation.primitive.AzRawAnimation;

public class MarauderAnimator extends AzEntityAnimator<MarauderEntity> {

    private static final ResourceLocation ANIMATIONS = AzureLib.modResource(
        "animations/entity/marauder.animation.json"
    );

    private static final String IDLE_ANIMATION_NAME = "idle";

    private static final String WALK_ANIMATION_NAME = "walk";

    private static final String SPAWN_ANIMATION_NAME = "spawn";

    private static final String DEATH_ANIMATION_NAME = "death";

    private static final String RUN_ANIMATION_NAME = "run";

    private static final String MELEE_ANIMATION_NAME = "axe_attack";

    private static final AzRawAnimation IDLE_ANIMATION = AzRawAnimation.begin().thenLoop(IDLE_ANIMATION_NAME);

    private static final AzRawAnimation WALK_ANIMATION = AzRawAnimation.begin().thenLoop(WALK_ANIMATION_NAME);

    private static final AzRawAnimation SPAWN_ANIMATION = AzRawAnimation.begin().then(SPAWN_ANIMATION_NAME, AzLoopType.PLAY_ONCE);

    private static final AzRawAnimation DEATH_ANIMATION = AzRawAnimation.begin()
        .then(DEATH_ANIMATION_NAME, AzLoopType.HOLD_ON_LAST_FRAME);

    private static final AzRawAnimation RUN_ANIMATION = AzRawAnimation.begin().thenLoop(RUN_ANIMATION_NAME);

    private static final AzRawAnimation MELEE_ANIMATION = AzRawAnimation.begin()
        .then(MELEE_ANIMATION_NAME, AzLoopType.PLAY_ONCE);

    public MarauderAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<MarauderEntity> animationControllerContainer) {
        animationControllerContainer.add(
            AzAnimationController.builder(this, "base_controller")
                .setTransitionLength(5)
                .setKeyFrameCallbacks(
                    AzKeyFrameCallbacks.<MarauderEntity>builder()
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
                .triggerableAnim(IDLE_ANIMATION_NAME, IDLE_ANIMATION)
                .triggerableAnim(WALK_ANIMATION_NAME, WALK_ANIMATION)
                .triggerableAnim(RUN_ANIMATION_NAME, RUN_ANIMATION)
                .triggerableAnim(MELEE_ANIMATION_NAME, MELEE_ANIMATION)
                .triggerableAnim(DEATH_ANIMATION_NAME, DEATH_ANIMATION)
                .triggerableAnim(SPAWN_ANIMATION_NAME, SPAWN_ANIMATION)
                .build()
        );
    }

    @Override
    public @NotNull ResourceLocation getAnimationLocation(MarauderEntity drone) {
        return ANIMATIONS;
    }
}
