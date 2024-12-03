package mod.azure.azurelib.core2.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        return bones.stream()
            .collect(
                Collectors.toMap(
                    AzBone::getName,
                    Function.identity(),
                    (left, right) -> right
                )
            );
    }
}
