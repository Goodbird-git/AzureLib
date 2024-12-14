package mod.azure.azurelib.core2.animation;

import mod.azure.azurelib.core.animation.EasingType;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzBoneAnimationQueue;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.model.AzBoneSnapshot;

public class AzBoneAnimationUpdateUtil {

    public static void updatePositions(
        AzBoneAnimationQueue boneAnimation,
        AzBone bone,
        EasingType easingType,
        AzBoneSnapshot snapshot
    ) {
        var posXPoint = boneAnimation.positionXQueue().poll();
        var posYPoint = boneAnimation.positionYQueue().poll();
        var posZPoint = boneAnimation.positionZQueue().poll();

        if (posXPoint != null && posYPoint != null && posZPoint != null) {
            bone.setPosX((float) EasingType.lerpWithOverride(posXPoint, easingType));
            bone.setPosY((float) EasingType.lerpWithOverride(posYPoint, easingType));
            bone.setPosZ((float) EasingType.lerpWithOverride(posZPoint, easingType));
            snapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
            snapshot.startPosAnim();
            bone.markPositionAsChanged();
        }
    }

    public static void updateRotations(
        AzBoneAnimationQueue boneAnimation,
        AzBone bone,
        EasingType easingType,
        AzBoneSnapshot initialSnapshot,
        AzBoneSnapshot snapshot
    ) {
        var rotXPoint = boneAnimation.rotationXQueue().poll();
        var rotYPoint = boneAnimation.rotationYQueue().poll();
        var rotZPoint = boneAnimation.rotationZQueue().poll();

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

    public static void updateScale(
        AzBoneAnimationQueue boneAnimation,
        AzBone bone,
        EasingType easingType,
        AzBoneSnapshot snapshot
    ) {
        var scaleXPoint = boneAnimation.scaleXQueue().poll();
        var scaleYPoint = boneAnimation.scaleYQueue().poll();
        var scaleZPoint = boneAnimation.scaleZQueue().poll();

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
