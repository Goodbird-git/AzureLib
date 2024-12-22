package mod.azure.azurelib.model;

import java.util.*;

/**
 * Represents a baked 3D model consisting of hierarchical bone structures.
 * This class is immutable and provides read-only access to bones by name
 * or as a list of top-level bones. Bones are uniquely identified by their names.
 */
public class AzBakedModel {

    public static final AzBakedModel EMPTY = new AzBakedModel(Arrays.asList());

    private final Map<String, AzBone> bonesByName;

    private final List<AzBone> topLevelBones;

    public AzBakedModel(List<AzBone> topLevelBones) {
        this.topLevelBones = Collections.unmodifiableList(topLevelBones);
        this.bonesByName = Collections.unmodifiableMap(mapBonesByName(topLevelBones));
    }

    private Map<String, AzBone> mapBonesByName(List<AzBone> bones) {
        HashMap<String, AzBone> bonesByName = new HashMap<String, AzBone>();
        ArrayDeque<AzBone> nodesToMap = new ArrayDeque<>(bones);

        while (!nodesToMap.isEmpty()) {
            AzBone currentBone = nodesToMap.poll();
            nodesToMap.addAll(currentBone.getChildBones());
            currentBone.saveInitialSnapshot();
            bonesByName.put(currentBone.getName(), currentBone);
        }

        return bonesByName;
    }

    public AzBone getBoneOrNull(String name) {
        return bonesByName.get(name);
    }

    public Optional<AzBone> getBone(String name) {
        return Optional.ofNullable(getBoneOrNull(name));
    }

    public Map<String, AzBone> getBonesByName() {
        return bonesByName;
    }

    public List<AzBone> getTopLevelBones() {
        return topLevelBones;
    }
}
