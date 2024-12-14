package mod.azure.azurelib.core2.model;

import org.jetbrains.annotations.Nullable;

import mod.azure.azurelib.common.internal.common.loading.json.raw.Bone;

public record AzBoneMetadata(
    @Nullable Boolean dontRender,
    @Nullable Double inflate,
    Boolean mirror,
    String name,
    @Nullable AzBone parent,
    @Nullable Boolean reset
) {

    public AzBoneMetadata(Bone bone, AzBone parent) {
        this(bone.neverRender(), bone.inflate(), bone.mirror(), bone.name(), parent, bone.reset());
    }
}
