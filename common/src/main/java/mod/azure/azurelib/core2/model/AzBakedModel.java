package mod.azure.azurelib.core2.model;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AzBakedModel {

    private final Map<String, AzBone> bonesByName;

    private final ResourceLocation resourceLocation;

    private final List<AzBone> topLevelBones;

    public AzBakedModel(ResourceLocation resourceLocation, List<AzBone> topLevelBones) {
        this.resourceLocation = resourceLocation;
        this.topLevelBones = Collections.unmodifiableList(topLevelBones);
        this.bonesByName = Collections.unmodifiableMap(mapBonesByName(topLevelBones));
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

    public Optional<AzBone> getBoneByName(String name) {
        return Optional.ofNullable(bonesByName.get(name));
    }

    public Map<String, AzBone> getBonesByName() {
        return bonesByName;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }

    public List<AzBone> getTopLevelBones() {
        return topLevelBones;
    }
}
