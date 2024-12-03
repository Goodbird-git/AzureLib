package mod.azure.azurelib.core2.animation;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import mod.azure.azurelib.core.animation.EasingType;
import mod.azure.azurelib.core.state.BoneSnapshot;
import mod.azure.azurelib.core.utils.Interpolations;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.primitive.AzAnimation;
import mod.azure.azurelib.core2.animation.primitive.AzQueuedAnimation;
import mod.azure.azurelib.core2.animation.primitive.AzRawAnimation;
import mod.azure.azurelib.core2.animation.primitive.AzStage;
import mod.azure.azurelib.core2.model.AzBakedModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AzAnimationProcessor<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzAnimationProcessor.class);

    private final AzAnimator<T> animator;
    private final Map<String, CoreGeoBone> bonesByName;

    public boolean reloadAnimations;

    public AzAnimationProcessor(AzAnimator<T> animator) {
        this.animator = animator;
        this.bonesByName = new Object2ObjectOpenHashMap<>();
        this.reloadAnimations = false;
    }

    /**
     * Build an animation queue for the given {@link AzRawAnimation}
     *
     * @param animatable   The animatable object being rendered
     * @param rawAnimation The raw animation to be compiled
     * @return A queue of animations and loop types to play
     */
    public Queue<AzQueuedAnimation> buildAnimationQueue(T animatable, AzRawAnimation rawAnimation) {
        var animations = new LinkedList<AzQueuedAnimation>();

        for (var stage : rawAnimation.getAnimationStages()) {
            AzAnimation animation;

            if (Objects.equals(stage.animationName(), AzStage.WAIT)) {
                animation = AzAnimation.generateWaitAnimation(stage.additionalTicks());
            } else {
                animation = animator.getAnimation(animatable, stage.animationName());
            }

            if (animation == null) {
                LOGGER.warn(
                        "Unable to find animation: {} for {}",
                        stage.animationName(),
                        animatable.getClass().getSimpleName()
                );
                return null;
            } else {
                animations.add(new AzQueuedAnimation(animation, stage.loopType()));
            }
        }

        return animations;
    }

    /**
     * Tick and apply transformations to the model based on the current state of the {@link AzAnimationController}
     *
     * @param animatable The animatable object relevant to the animation being played
     */
    public void tickAnimation(T animatable, AzAnimationState<T> event) {
        var animTime = animator.getAnimTime();
        var boneSnapshotCollection = animator.getBoneSnapshotCache().getAll();
        // TODO: This mutates the bone snapshot cache in a way that is... well, terrible. find another way!
        var boneSnapshots = updateBoneSnapshots(boneSnapshotCollection);

        for (var controller : animator.getAnimationControllerContainer().getAll()) {
            if (this.reloadAnimations) {
                controller.forceAnimationReset();
                controller.getBoneAnimationQueues().clear();
            }

            controller.setJustStarting(animator.isFirstTick());

            event.withController(controller);
            controller.process(event, this.bonesByName, boneSnapshots, animTime, animator.crashIfBoneMissing());

            for (var boneAnimation : controller.getBoneAnimationQueues().values()) {
                var bone = boneAnimation.bone();
                var snapshot = boneSnapshots.get(bone.getName());
                var initialSnapshot = bone.getInitialSnapshot();

                var rotXPoint = boneAnimation.rotationXQueue().poll();
                var rotYPoint = boneAnimation.rotationYQueue().poll();
                var rotZPoint = boneAnimation.rotationZQueue().poll();
                var posXPoint = boneAnimation.positionXQueue().poll();
                var posYPoint = boneAnimation.positionYQueue().poll();
                var posZPoint = boneAnimation.positionZQueue().poll();
                var scaleXPoint = boneAnimation.scaleXQueue().poll();
                var scaleYPoint = boneAnimation.scaleYQueue().poll();
                var scaleZPoint = boneAnimation.scaleZQueue().poll();
                var easingType = controller.getOverrideEasingTypeFunction().apply(animatable);

                if (rotXPoint != null && rotYPoint != null && rotZPoint != null) {
                    bone.setRotX(
                            (float) EasingType.lerpWithOverride(rotXPoint, easingType) + initialSnapshot.getRotX()
                    );
                    bone.setRotY(
                            (float) EasingType.lerpWithOverride(rotYPoint, easingType) + initialSnapshot.getRotY()
                    );
                    bone.setRotZ(
                            (float) EasingType.lerpWithOverride(rotZPoint, easingType) + initialSnapshot.getRotZ()
                    );
                    snapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
                    snapshot.startRotAnim();
                    bone.markRotationAsChanged();
                }

                if (posXPoint != null && posYPoint != null && posZPoint != null) {
                    bone.setPosX((float) EasingType.lerpWithOverride(posXPoint, easingType));
                    bone.setPosY((float) EasingType.lerpWithOverride(posYPoint, easingType));
                    bone.setPosZ((float) EasingType.lerpWithOverride(posZPoint, easingType));
                    snapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
                    snapshot.startPosAnim();
                    bone.markPositionAsChanged();
                }

                if (scaleXPoint != null && scaleYPoint != null && scaleZPoint != null) {
                    bone.setScaleX((float) EasingType.lerpWithOverride(scaleXPoint, easingType));
                    bone.setScaleY((float) EasingType.lerpWithOverride(scaleYPoint, easingType));
                    bone.setScaleZ((float) EasingType.lerpWithOverride(scaleZPoint, easingType));
                    snapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
                    snapshot.startScaleAnim();
                    bone.markScaleAsChanged();
                }
            }
        }

        this.reloadAnimations = false;
        double resetTickLength = animator.getBoneResetTime();

        for (var bone : getRegisteredBones()) {
            if (!bone.hasRotationChanged()) {
                var initialSnapshot = bone.getInitialSnapshot();
                var saveSnapshot = boneSnapshots.get(bone.getName());

                if (saveSnapshot.isRotAnimInProgress())
                    saveSnapshot.stopRotAnim(animTime);

                double percentageReset = Math.min(
                        (animTime - saveSnapshot.getLastResetRotationTick()) / resetTickLength,
                        1
                );

                bone.setRotX(
                        (float) Interpolations.lerp(saveSnapshot.getRotX(), initialSnapshot.getRotX(), percentageReset)
                );
                bone.setRotY(
                        (float) Interpolations.lerp(saveSnapshot.getRotY(), initialSnapshot.getRotY(), percentageReset)
                );
                bone.setRotZ(
                        (float) Interpolations.lerp(saveSnapshot.getRotZ(), initialSnapshot.getRotZ(), percentageReset)
                );

                if (percentageReset >= 1) {
                    saveSnapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
                }
            }

            if (!bone.hasPositionChanged()) {
                var initialSnapshot = bone.getInitialSnapshot();
                var saveSnapshot = boneSnapshots.get(bone.getName());

                if (saveSnapshot.isPosAnimInProgress()) {
                    saveSnapshot.stopPosAnim(animTime);
                }

                var percentageReset = Math.min((animTime - saveSnapshot.getLastResetPositionTick()) / resetTickLength, 1);

                bone.setPosX(
                    (float) Interpolations.lerp(
                        saveSnapshot.getOffsetX(),
                        initialSnapshot.getOffsetX(),
                        percentageReset
                    )
                );
                bone.setPosY(
                    (float) Interpolations.lerp(
                        saveSnapshot.getOffsetY(),
                        initialSnapshot.getOffsetY(),
                        percentageReset
                    )
                );
                bone.setPosZ(
                    (float) Interpolations.lerp(
                        saveSnapshot.getOffsetZ(),
                        initialSnapshot.getOffsetZ(),
                        percentageReset
                    )
                );

                if (percentageReset >= 1) {
                    saveSnapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
                }
            }

            if (!bone.hasScaleChanged()) {
                var initialSnapshot = bone.getInitialSnapshot();
                var saveSnapshot = boneSnapshots.get(bone.getName());

                if (saveSnapshot.isScaleAnimInProgress()) {
                    saveSnapshot.stopScaleAnim(animTime);
                }

                double percentageReset = Math.min((animTime - saveSnapshot.getLastResetScaleTick()) / resetTickLength, 1);

                bone.setScaleX(
                    (float) Interpolations.lerp(saveSnapshot.getScaleX(), initialSnapshot.getScaleX(), percentageReset)
                );
                bone.setScaleY(
                    (float) Interpolations.lerp(saveSnapshot.getScaleY(), initialSnapshot.getScaleY(), percentageReset)
                );
                bone.setScaleZ(
                    (float) Interpolations.lerp(saveSnapshot.getScaleZ(), initialSnapshot.getScaleZ(), percentageReset)
                );

                if (percentageReset >= 1) {
                    saveSnapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
                }
            }
        }

        resetBoneTransformationMarkers();
        animator.finishFirstTick();
    }

    /**
     * Reset the transformation markers applied to each {@link CoreGeoBone} ready for the next render frame
     */
    private void resetBoneTransformationMarkers() {
        getRegisteredBones().forEach(CoreGeoBone::resetStateChanges);
    }

    /**
     * Create new bone {@link BoneSnapshot} based on the bone's initial snapshot for the currently registered
     * {@link CoreGeoBone GeoBones}, filtered by the bones already present in the master snapshots map
     *
     * @param snapshots The master bone snapshots map.
     * @return The input snapshots map, for easy assignment
     */
    private Map<String, BoneSnapshot> updateBoneSnapshots(Map<String, BoneSnapshot> snapshots) {
        for (var bone : getRegisteredBones()) {
            if (!snapshots.containsKey(bone.getName())) {
                snapshots.put(bone.getName(), BoneSnapshot.copy(bone.getInitialSnapshot()));
            }
        }

        return snapshots;
    }

    /**
     * Gets a bone by name.
     *
     * @param boneName The bone name
     * @return the bone
     */
    public CoreGeoBone getBone(String boneName) {
        return this.bonesByName.get(boneName);
    }

    /**
     * Adds the given bone to the bones list for this processor.<br>
     * This is normally handled automatically by AzureLib.<br>
     * Failure to properly register a bone will break things.
     */
    public void registerGeoBone(CoreGeoBone bone) {
        bone.saveInitialSnapshot();
        this.bonesByName.put(bone.getName(), bone);
        bone.getChildBones().forEach(this::registerGeoBone);
    }

    /**
     * Clear the {@link CoreGeoBone GeoBones} currently registered to the processor, then prepares the processor for a
     * new model.<br>
     * Should be called whenever switching models to render/animate
     */
    public void setActiveModel(AzBakedModel model) {
        this.bonesByName.clear();
        model.getTopLevelBones().forEach(this::registerGeoBone);
    }

    /**
     * Get an iterable collection of the {@link CoreGeoBone GeoBones} currently registered to the processor
     */
    public Collection<CoreGeoBone> getRegisteredBones() {
        return this.bonesByName.values();
    }
}
