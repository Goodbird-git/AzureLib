package mod.azure.azurelib.core2.animation;

import mod.azure.azurelib.core2.animation.controller.keyframe.AzBoneAnimationQueue;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.model.AzBoneSnapshot;

public class AzBoneAnimationUpdateUtil {

    public static void updatePositions(
        AzBoneAnimationQueue boneAnimation,
        AzBone bone,
        AzEasingType easingType,
        AzBoneSnapshot snapshot
    ) {
        var posXPoint = boneAnimation.positionXQueue();
        var posYPoint = boneAnimation.positionYQueue();
        var posZPoint = boneAnimation.positionZQueue();

        if (!posXPoint.isEmpty() && !posYPoint.isEmpty() && !posZPoint.isEmpty()) {
            bone.setPosX((float) AzEasingType.lerpWithOverride(posXPoint, easingType));
            bone.setPosY((float) AzEasingType.lerpWithOverride(posYPoint, easingType));
            bone.setPosZ((float) AzEasingType.lerpWithOverride(posZPoint, easingType));
            snapshot.updateOffset(bone.getPosX(), bone.getPosY(), bone.getPosZ());
            snapshot.startPosAnim();
            bone.markPositionAsChanged();
        }
    }

    public static void updateRotations(
        AzBoneAnimationQueue boneAnimation,
        AzBone bone,
        AzEasingType easingType,
        AzBoneSnapshot initialSnapshot,
        AzBoneSnapshot snapshot
    ) {
        var rotXPoint = boneAnimation.rotationXQueue();
        var rotYPoint = boneAnimation.rotationYQueue();
        var rotZPoint = boneAnimation.rotationZQueue();

        if (!rotXPoint.isEmpty() && !rotYPoint.isEmpty() && !rotZPoint.isEmpty()) {
            bone.setRotX(
                (float) AzEasingType.lerpWithOverride(rotXPoint, easingType) + initialSnapshot.getRotX()
            );
            bone.setRotY(
                (float) AzEasingType.lerpWithOverride(rotYPoint, easingType) + initialSnapshot.getRotY()
            );
            bone.setRotZ(
                (float) AzEasingType.lerpWithOverride(rotZPoint, easingType) + initialSnapshot.getRotZ()
            );
            snapshot.updateRotation(bone.getRotX(), bone.getRotY(), bone.getRotZ());
            snapshot.startRotAnim();
            bone.markRotationAsChanged();
        }
    }

    public static void updateScale(
        AzBoneAnimationQueue boneAnimation,
        AzBone bone,
        AzEasingType easingType,
        AzBoneSnapshot snapshot
    ) {
        var scaleXPoint = boneAnimation.scaleXQueue();
        var scaleYPoint = boneAnimation.scaleYQueue();
        var scaleZPoint = boneAnimation.scaleZQueue();

        if (!scaleXPoint.isEmpty() && !scaleYPoint.isEmpty() && !scaleZPoint.isEmpty()) {
            bone.setScaleX((float) AzEasingType.lerpWithOverride(scaleXPoint, easingType));
            bone.setScaleY((float) AzEasingType.lerpWithOverride(scaleYPoint, easingType));
            bone.setScaleZ((float) AzEasingType.lerpWithOverride(scaleZPoint, easingType));
            snapshot.updateScale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
            snapshot.startScaleAnim();
            bone.markScaleAsChanged();
        }
    }
}
