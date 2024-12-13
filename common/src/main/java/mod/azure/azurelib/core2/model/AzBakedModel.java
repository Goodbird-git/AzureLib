package mod.azure.azurelib.core2.model;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AzBakedModel {

    private final Map<String, AzBone> bonesByName;

    private final List<AzBone> topLevelBones;

    public AzBakedModel(List<AzBone> topLevelBones) {
        this.topLevelBones = Collections.unmodifiableList(topLevelBones);
        this.bonesByName = Collections.unmodifiableMap(mapBonesByName(topLevelBones));
    }

    public List<AzBone> getTopLevelBones() {
        return topLevelBones;
    }

    public Optional<AzBone> getBoneByName(String name) {
        return Optional.ofNullable(bonesByName.get(name));
    }

    private Map<String, AzBone> mapBonesByName(List<AzBone> bones) {
        var bonesByName = new HashMap<String, AzBone>();
        var nodesToMap = new ArrayDeque<>(bones);

        while (!nodesToMap.isEmpty()) {
            var currentBone = nodesToMap.poll();
            nodesToMap.addAll(currentBone.getChildBones());
            bonesByName.put(currentBone.getName(), currentBone);
        }

        return bonesByName;
    }
}
