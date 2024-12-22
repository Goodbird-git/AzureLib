package mod.azure.azurelib.model;

import mod.azure.azurelib.loading.json.raw.Bone;

/**
 * AzBoneMetadata is a record class representing metadata about a 3D model bone.
 * This metadata provides information such as rendering preferences, inflation values,
 * mirroring, hierarchy, and reset options for a bone in a 3D model's structure.
 */
public class AzBoneMetadata {

    private Boolean dontRender;

    private Double inflate;

    private Boolean mirror;

    private String name;

    private AzBone parent;

    private Boolean reset;

    // Constructor with parameters
    public AzBoneMetadata(Boolean dontRender, Double inflate, Boolean mirror, String name,
                          AzBone parent, Boolean reset) {
        this.dontRender = dontRender;
        this.inflate = inflate;
        this.mirror = mirror;
        this.name = name;
        this.parent = parent;
        this.reset = reset;
    }

    // Additional constructor for Bone input
    public AzBoneMetadata(Bone bone, AzBone parent) {
        this(bone.neverRender(), bone.inflate(), bone.mirror(), bone.name(), parent, bone.reset());
    }

    // Getters
    public Boolean dontRender() {
        return dontRender;
    }

    public Double inflate() {
        return inflate;
    }

    public Boolean mirror() {
        return mirror;
    }

    public String name() {
        return name;
    }

    public AzBone parent() {
        return parent;
    }

    public Boolean reset() {
        return reset;
    }

    public void setDontRender(Boolean dontRender) {
        this.dontRender = dontRender;
    }

    public void setInflate(Double inflate) {
        this.inflate = inflate;
    }

    public void setMirror(Boolean mirror) {
        this.mirror = mirror;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(AzBone parent) {
        this.parent = parent;
    }

    public void setReset(Boolean reset) {
        this.reset = reset;
    }
}
