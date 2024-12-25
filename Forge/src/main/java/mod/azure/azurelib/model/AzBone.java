/**
 * This class is a fork of the matching class found in the Geckolib repository. Original source:
 * https://github.com/bernie-g/geckolib Copyright © 2024 Bernie-G. Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.model;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.azure.azurelib.cache.object.GeoCube;
import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import mod.azure.azurelib.core.state.BoneSnapshot;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Objects;

/**
 * Mutable bone object representing a set of cubes, as well as child bones.<br>
 * This is the object that is directly modified by animations to handle movement
 */
public class AzBone implements CoreGeoBone {

    private final AzBoneMetadata metadata;

    private final List<AzBone> children = new ObjectArrayList<>();

    private final List<GeoCube> cubes = new ObjectArrayList<>();

    private AzBoneSnapshot initialSnapshot;

    private boolean hidden;

    private boolean childrenHidden = false;

    private float scaleX = 1;
    private float scaleY = 1;
    private float scaleZ = 1;

    private float positionX;
    private float positionY;
    private float positionZ;

    private float rotationPointX;
    private float rotationPointY;
    private float rotationPointZ;

    private float rotateX;
    private float rotateY;
    private float rotateZ;

    private boolean positionChanged = false;

    private boolean rotationChanged = false;

    private boolean scaleChanged = false;

    public AzBone(AzBoneMetadata metadata) {
        this.metadata = metadata;
        this.hidden = metadata.dontRender() == Boolean.TRUE;
    }

    @Override
    public String getName() {
        return metadata.name();
    }

    @Override
    public AzBone getParent() {
        return metadata.parent();
    }

    @Override
    public float getRotX() {
        return (float) this.rotateX;
    }

    @Override
    public void setRotX(float value) {
        this.rotateX = value;

        markRotationAsChanged();
    }

    @Override
    public float getRotY() {
        return (float) this.rotateY;
    }

    @Override
    public void setRotY(float value) {
        this.rotateY = value;

        markRotationAsChanged();
    }

    @Override
    public float getRotZ() {
        return (float) this.rotateZ;
    }

    @Override
    public void setRotZ(float value) {
        this.rotateZ = value;

        markRotationAsChanged();
    }

    @Override
    public float getPosX() {
        return (float) this.positionX;
    }

    @Override
    public void setPosX(float value) {
        this.positionX = value;

        markPositionAsChanged();
    }

    @Override
    public float getPosY() {
        return (float) this.positionY;
    }

    @Override
    public void setPosY(float value) {
        this.positionY = value;

        markPositionAsChanged();
    }

    @Override
    public float getPosZ() {
        return (float) this.positionZ;
    }

    @Override
    public void setPosZ(float value) {
        this.positionZ = value;

        markPositionAsChanged();
    }

    @Override
    public float getScaleX() {
        return (float) this.scaleX;
    }

    @Override
    public void setScaleX(float value) {
        this.scaleX = value;

        markScaleAsChanged();
    }

    @Override
    public float getScaleY() {
        return (float) this.scaleY;
    }

    @Override
    public void setScaleY(float value) {
        this.scaleY = value;

        markScaleAsChanged();
    }

    @Override
    public float getScaleZ() {
        return (float) this.scaleZ;
    }

    @Override
    public void setScaleZ(float value) {
        this.scaleZ = value;

        markScaleAsChanged();
    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;

        setChildrenHidden(hidden);
    }

    @Override
    public void setChildrenHidden(boolean hideChildren) {
        this.childrenHidden = hideChildren;
    }

    @Override
    public float getPivotX() {
        return this.rotationPointX;
    }

    @Override
    public void setPivotX(float value) {
        this.rotationPointX = value;
    }

    @Override
    public float getPivotY() {
        return this.rotationPointY;
    }

    @Override
    public void setPivotY(float value) {
        this.rotationPointY = value;
    }

    @Override
    public float getPivotZ() {
        return this.rotationPointZ;
    }

    @Override
    public void setPivotZ(float value) {
        this.rotationPointZ = value;
    }

    @Override
    public boolean isHidingChildren() {
        return this.childrenHidden;
    }

    @Override
    public void markScaleAsChanged() {
        this.scaleChanged = true;
    }

    @Override
    public void markRotationAsChanged() {
        this.rotationChanged = true;
    }

    @Override
    public void markPositionAsChanged() {
        this.positionChanged = true;
    }

    @Override
    public boolean hasScaleChanged() {
        return this.scaleChanged;
    }

    @Override
    public boolean hasRotationChanged() {
        return this.rotationChanged;
    }

    @Override
    public boolean hasPositionChanged() {
        return this.positionChanged;
    }

    @Override
    public void resetStateChanges() {
        this.scaleChanged = false;
        this.rotationChanged = false;
        this.positionChanged = false;
    }

    @Override
    public AzBoneSnapshot getInitialAzSnapshot() {
        return this.initialSnapshot;
    }

    @Override
    public List<AzBone> getChildBones() {
        return this.children;
    }

    @Override
    public void saveInitialSnapshot() {
        if (this.initialSnapshot == null) {
            this.initialSnapshot = new AzBoneSnapshot(this);
        }
    }

    public Boolean getMirror() {
        return metadata.mirror();
    }

    public Double getInflate() {
        return metadata.inflate();
    }

    public Boolean shouldNeverRender() {
        return metadata.dontRender();
    }

    public Boolean getReset() {
        return metadata.reset();
    }

    public List<GeoCube> getCubes() {
        return this.cubes;
    }

    public void addRotationOffsetFromBone(AzBone source) {
        setRotX(getRotX() + source.getRotX() - source.getInitialAzSnapshot().getRotX());
        setRotY(getRotY() + source.getRotY() - source.getInitialAzSnapshot().getRotY());
        setRotZ(getRotZ() + source.getRotZ() - source.getInitialAzSnapshot().getRotZ());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        return hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            getName(),
            (getParent() != null ? getParent().getName() : 0),
            getCubes().size(),
            getChildBones().size()
        );
    }
}
