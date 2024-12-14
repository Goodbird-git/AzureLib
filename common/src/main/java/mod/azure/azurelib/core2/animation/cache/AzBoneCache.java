package mod.azure.azurelib.core2.animation.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Collection;
import java.util.Map;

import mod.azure.azurelib.core2.animation.AzAnimationContext;
import mod.azure.azurelib.core2.animation.AzCachedBoneUpdateUtil;
import mod.azure.azurelib.core2.model.AzBakedModel;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.model.AzBoneSnapshot;

public class AzBoneCache {

    private AzBakedModel bakedModel;

    private final Map<String, AzBoneSnapshot> boneSnapshotsByName;

    public AzBoneCache() {
        this.bakedModel = AzBakedModel.EMPTY;
        this.boneSnapshotsByName = new Object2ObjectOpenHashMap<>();
    }

    /**
     * Create new bone {@link AzBoneSnapshot} based on the bone's initial snapshot for the currently registered
     * {@link AzBone AzBones}, filtered by the bones already present in the master snapshots map
     */
    public void snapshot() {
        for (var bone : getRegisteredBones()) {
            boneSnapshotsByName.computeIfAbsent(bone.getName(), $ -> AzBoneSnapshot.copy(bone.getInitialAzSnapshot()));
        }
    }

    /**
     * Sets the current model from which to
     */
    public void setActiveModel(AzBakedModel model) {
        this.bakedModel = model;
    }

    public void update(AzAnimationContext<?> context) {
        var config = context.config();
        var timer = context.timer();
        var animTime = timer.getAnimTime();
        var boneSnapshots = getBoneSnapshotsByName();
        var resetTickLength = config.boneResetTime();

        // Updates the cached bone snapshots (only if they have changed).
        for (var bone : getRegisteredBones()) {
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
        getRegisteredBones().forEach(AzBone::resetStateChanges);
    }

    /**
     * Get an iterable collection of the {@link AzBone AzBones} currently registered to the processor
     */
    private Collection<AzBone> getRegisteredBones() {
        return getBonesByName().values();
    }

    public Map<String, AzBone> getBonesByName() {
        return bakedModel.getBonesByName();
    }

    public Map<String, AzBoneSnapshot> getBoneSnapshotsByName() {
        return boneSnapshotsByName;
    }

    public boolean isEmpty() {
        return getRegisteredBones().isEmpty();
    }
}
