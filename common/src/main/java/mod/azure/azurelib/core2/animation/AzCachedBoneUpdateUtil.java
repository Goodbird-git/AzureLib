package mod.azure.azurelib.core2.animation;

import java.util.Map;

import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import mod.azure.azurelib.core.state.BoneSnapshot;
import mod.azure.azurelib.core.utils.Interpolations;

public class AzCachedBoneUpdateUtil {

    public static void updateCachedBonePosition(
        CoreGeoBone bone,
        Map<String, BoneSnapshot> boneSnapshots,
        double animTime,
        double resetTickLength
    ) {
        if (bone.hasPositionChanged()) {
            return;
        }

        var initialSnapshot = bone.getInitialSnapshot();
        var saveSnapshot = boneSnapshots.get(bone.getName());

        if (saveSnapshot.isPosAnimInProgress()) {
            saveSnapshot.stopPosAnim(animTime);
        }

        var percentageReset = Math.min(
            (animTime - saveSnapshot.getLastResetPositionTick()) / resetTickLength,
            1
        );

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

    public static void updateCachedBoneRotation(
        CoreGeoBone bone,
        Map<String, BoneSnapshot> boneSnapshots,
        double animTime,
        double resetTickLength
    ) {
        if (bone.hasRotationChanged()) {
            return;
        }

        var initialSnapshot = bone.getInitialSnapshot();
        var saveSnapshot = boneSnapshots.get(bone.getName());

        if (saveSnapshot.isRotAnimInProgress()) {
            saveSnapshot.stopRotAnim(animTime);
        }

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

    public static void updateCachedBoneScale(
        CoreGeoBone bone,
        Map<String, BoneSnapshot> boneSnapshots,
        double animTime,
        double resetTickLength
    ) {
        if (bone.hasScaleChanged()) {
            return;
        }

        var initialSnapshot = bone.getInitialSnapshot();
        var saveSnapshot = boneSnapshots.get(bone.getName());

        if (saveSnapshot.isScaleAnimInProgress()) {
            saveSnapshot.stopScaleAnim(animTime);
        }

        double percentageReset = Math.min(
            (animTime - saveSnapshot.getLastResetScaleTick()) / resetTickLength,
            1
        );

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
