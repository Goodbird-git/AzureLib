package mod.azure.azurelib.animation;

import mod.azure.azurelib.animation.controller.keyframe.AzBoneAnimationQueue;
import mod.azure.azurelib.core.animation.EasingType;
import mod.azure.azurelib.core.keyframe.AnimationPoint;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.model.AzBoneSnapshot;

public class AzBoneAnimationUpdateUtil {

    /**
     * Updates the position of the given bone by interpolating the position values from the animation queue and applying
     * the specified easing type. The method also updates the snapshot offsets and flags the bone's position as changed.
     *
     * @param boneAnimation The animation queue containing position data for the bone.
     * @param bone          The bone whose position is being updated.
     * @param easingType    The easing type used for interpolating the position values.
     * @param snapshot      The snapshot used to store the updated position offsets and start animations.
     */
    public static void updatePositions(
        AzBoneAnimationQueue boneAnimation,
        AzBone bone,
        EasingType easingType,
        AzBoneSnapshot snapshot
    ) {
        AnimationPoint posXPoint = boneAnimation.positionXQueue().poll();
        AnimationPoint posYPoint = boneAnimation.positionYQueue().poll();
        AnimationPoint posZPoint = boneAnimation.positionZQueue().poll();

        if (posXPoint != null && posYPoint != null && posZPoint != null) {
            bone.setPosX((float) EasingType.lerpWithOverride(posXPoint, easingType));
            bone.setPosY((float) EasingType.lerpWithOverride(posYPoint, easingType));
            bone.setPosZ((float) EasingType.lerpWithOverride(posZPoint, easingType));
            snapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
            snapshot.startPosAnim();
            bone.markPositionAsChanged();
        }
    }

    /**
     * Updates the rotation of the specified bone by interpolating the rotation values from the animation queue and
     * applying the specified easing type. The method also updates the snapshot rotation values, starts the rotation
     * animation, and marks the bone's rotation as changed.
     *
     * @param boneAnimation   The animation queue containing rotation data for the bone.
     * @param bone            The bone whose rotation is being updated.
     * @param easingType      The easing type used for interpolating the rotation values.
     * @param initialSnapshot The initial snapshot containing the original rotation offsets.
     * @param snapshot        The snapshot used to store the updated rotation values and start animations.
     */
    public static void updateRotations(
        AzBoneAnimationQueue boneAnimation,
        AzBone bone,
        EasingType easingType,
        AzBoneSnapshot initialSnapshot,
        AzBoneSnapshot snapshot
    ) {
        AnimationPoint rotXPoint = boneAnimation.rotationXQueue().poll();
        AnimationPoint rotYPoint = boneAnimation.rotationYQueue().poll();
        AnimationPoint rotZPoint = boneAnimation.rotationZQueue().poll();

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
    }

    /**
     * Updates the scale of the specified bone by interpolating the scale values from the animation queue and applying
     * the specified easing type. The method also updates the snapshot with the new scale values, starts the scale
     * animation, and marks the bone's scale as changed.
     *
     * @param boneAnimation The animation queue containing scale data for the bone.
     * @param bone          The bone whose scale is being updated.
     * @param easingType    The easing type used for interpolating the scale values.
     * @param snapshot      The snapshot used to store the updated scale values and start animations.
     */
    public static void updateScale(
        AzBoneAnimationQueue boneAnimation,
        AzBone bone,
        EasingType easingType,
        AzBoneSnapshot snapshot
    ) {
        AnimationPoint scaleXPoint = boneAnimation.scaleXQueue().poll();
        AnimationPoint scaleYPoint = boneAnimation.scaleYQueue().poll();
        AnimationPoint scaleZPoint = boneAnimation.scaleZQueue().poll();

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
