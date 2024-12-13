package mod.azure.azurelib.core2.animation.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Collection;
import java.util.Map;

import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import mod.azure.azurelib.core.state.BoneSnapshot;
import mod.azure.azurelib.core2.model.AzBakedModel;

public class AzBoneCache {

    private final Map<String, CoreGeoBone> bonesByName;

    private final Map<String, BoneSnapshot> boneSnapshotsByName;

    public AzBoneCache() {
        this.bonesByName = new Object2ObjectOpenHashMap<>();
        this.boneSnapshotsByName = new Object2ObjectOpenHashMap<>();
    }

    /**
     * Create new bone {@link BoneSnapshot} based on the bone's initial snapshot for the currently registered
     * {@link CoreGeoBone GeoBones}, filtered by the bones already present in the master snapshots map
     *
     * @return The input snapshots map, for easy assignment
     */
    public void updateBoneSnapshots() {
        for (var bone : getRegisteredBones()) {
            if (!boneSnapshotsByName.containsKey(bone.getName())) {
                boneSnapshotsByName.put(bone.getName(), BoneSnapshot.copy(bone.getInitialSnapshot()));
            }
        }
    }

    /**
     * Get an iterable collection of the {@link CoreGeoBone GeoBones} currently registered to the processor
     */
    public Collection<CoreGeoBone> getRegisteredBones() {
        return this.bonesByName.values();
    }

    /**
     * Reset the transformation markers applied to each {@link CoreGeoBone} ready for the next render frame
     */
    public void resetBoneTransformationMarkers() {
        getRegisteredBones().forEach(CoreGeoBone::resetStateChanges);
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

    public Map<String, CoreGeoBone> getBonesByName() {
        return bonesByName;
    }

    public Map<String, BoneSnapshot> getBoneSnapshotsByName() {
        return boneSnapshotsByName;
    }
}
