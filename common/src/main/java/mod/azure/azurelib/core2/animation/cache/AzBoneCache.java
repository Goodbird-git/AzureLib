package mod.azure.azurelib.core2.animation.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Map;

import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import mod.azure.azurelib.core.state.BoneSnapshot;
import mod.azure.azurelib.core2.animation.AzAnimationContext;
import mod.azure.azurelib.core2.animation.AzCachedBoneUpdateUtil;
import mod.azure.azurelib.core2.model.AzBakedModel;

public class AzBoneCache {

    private final Map<String, CoreGeoBone> bonesByName;

    private final Map<String, BoneSnapshot> boneSnapshotsByName;

    private ResourceLocation cachedResourceLocation;

    public AzBoneCache() {
        this.bonesByName = new Object2ObjectOpenHashMap<>();
        this.boneSnapshotsByName = new Object2ObjectOpenHashMap<>();
    }

    /**
     * Create new bone {@link BoneSnapshot} based on the bone's initial snapshot for the currently registered
     * {@link CoreGeoBone GeoBones}, filtered by the bones already present in the master snapshots map
     */
    public void snapshot() {
        for (var bone : getRegisteredBones()) {
            boneSnapshotsByName.computeIfAbsent(bone.getName(), $ -> BoneSnapshot.copy(bone.getInitialSnapshot()));
        }
    }

    /**
     * Clear the {@link CoreGeoBone GeoBones} currently registered to the processor, then prepares the processor for a
     * new model.<br>
     * Should be called whenever switching models to render/animate
     */
    public void setActiveModel(AzBakedModel model) {
        var newModelResourceLocation = model.getResourceLocation();

        // If the cached resource location has not been set, or if the new resource location is different,
        // then flush the bone cache and repopulate it with the new model's bones.
        if (cachedResourceLocation == null || !cachedResourceLocation.equals(newModelResourceLocation)) {
            bonesByName.clear();
            model.getBonesByName().forEach(($, value) -> value.saveInitialSnapshot());
            bonesByName.putAll(model.getBonesByName());
            this.cachedResourceLocation = newModelResourceLocation;
        }
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
     * Reset the transformation markers applied to each {@link CoreGeoBone} ready for the next render frame
     */
    private void resetBoneTransformationMarkers() {
        getRegisteredBones().forEach(CoreGeoBone::resetStateChanges);
    }

    /**
     * Get an iterable collection of the {@link CoreGeoBone GeoBones} currently registered to the processor
     */
    private Collection<CoreGeoBone> getRegisteredBones() {
        return this.bonesByName.values();
    }

    public Map<String, CoreGeoBone> getBonesByName() {
        return bonesByName;
    }

    public Map<String, BoneSnapshot> getBoneSnapshotsByName() {
        return boneSnapshotsByName;
    }

    public boolean isEmpty() {
        return getRegisteredBones().isEmpty();
    }
}
