package mod.azure.azurelib.animation.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mod.azure.azurelib.animation.AzAnimationContext;
import mod.azure.azurelib.animation.AzAnimationTimer;
import mod.azure.azurelib.animation.AzAnimatorConfig;
import mod.azure.azurelib.animation.AzCachedBoneUpdateUtil;
import mod.azure.azurelib.model.AzBakedModel;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.model.AzBoneSnapshot;

import java.util.Map;
import java.util.Objects;

/**
 * The AzBoneCache class is responsible for managing the state and cache of bones in a baked model. It provides
 * functionality for updating animation contexts, managing snapshots of bone states, and resetting transformation
 * markers in preparation for rendering.
 */
public class AzBoneCache {

    private AzBakedModel bakedModel;

    private final Map<String, AzBoneSnapshot> boneSnapshotsByName;

    public AzBoneCache() {
        this.bakedModel = AzBakedModel.EMPTY;
        this.boneSnapshotsByName = new Object2ObjectOpenHashMap<>();
    }

    public boolean setActiveModel(AzBakedModel model) {
        boolean willModelChange = !Objects.equals(bakedModel, model);
        this.bakedModel = model;

        if (willModelChange) {
            snapshot();
        }

        return willModelChange;
    }

    public void update(AzAnimationContext<?> context) {
        AzAnimatorConfig config = context.config();
        AzAnimationTimer timer = context.timer();
        double animTime = timer.getAnimTime();
        Map<String, AzBoneSnapshot> boneSnapshots = getBoneSnapshotsByName();
        double resetTickLength = config.boneResetTime();

        // Updates the cached bone snapshots (only if they have changed).
        for (AzBone bone : bakedModel.getBonesByName().values()) {
            AzCachedBoneUpdateUtil.updateCachedBoneRotation(bone, boneSnapshots, animTime, resetTickLength);
            AzCachedBoneUpdateUtil.updateCachedBonePosition(bone, boneSnapshots, animTime, resetTickLength);
            AzCachedBoneUpdateUtil.updateCachedBoneScale(bone, boneSnapshots, animTime, resetTickLength);
        }

        resetBoneTransformationMarkers();
    }

    /**
     * Reset the transformation markers applied to each {@link AzBone} ready for the next render frame
     */
    private void resetBoneTransformationMarkers() {
        bakedModel.getBonesByName().values().forEach(AzBone::resetStateChanges);
    }

    /**
     * Create new bone {@link AzBoneSnapshot} based on the bone's initial snapshot for the currently registered
     * {@link AzBone AzBones}, filtered by the bones already present in the master snapshots map
     */
    private void snapshot() {
        boneSnapshotsByName.clear();

        for (AzBone bone : bakedModel.getBonesByName().values()) {
            boneSnapshotsByName.put(bone.getName(), AzBoneSnapshot.copy(bone.getInitialAzSnapshot()));
        }
    }

    public AzBakedModel getBakedModel() {
        return bakedModel;
    }

    public Map<String, AzBoneSnapshot> getBoneSnapshotsByName() {
        return boneSnapshotsByName;
    }

    public boolean isEmpty() {
        return bakedModel.getBonesByName().isEmpty();
    }
}
