package mod.azure.azurelib.core2.animation.cache;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import mod.azure.azurelib.core.state.BoneSnapshot;

import java.util.Map;

public class AzBoneSnapshotCache {

    private final Map<String, BoneSnapshot> boneSnapshotsByName;

    public AzBoneSnapshotCache() {
        this.boneSnapshotsByName = new Object2ObjectOpenHashMap<>();
    }

    public Map<String, BoneSnapshot> getBoneByName(String name) {
        return boneSnapshotsByName;
    }

    public void clear() {
        this.boneSnapshotsByName.clear();
    }

    public Map<String, BoneSnapshot> getAll() {
        return boneSnapshotsByName;
    }
}
