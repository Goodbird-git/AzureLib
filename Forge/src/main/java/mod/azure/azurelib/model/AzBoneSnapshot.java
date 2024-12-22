package mod.azure.azurelib.model;

import net.minecraft.util.math.Vec3d;

/**
 * A state monitoring class for a given {@link AzBone}.<br>
 */
public class AzBoneSnapshot {

    private final AzBone bone;

    private final Vec3d offsetPosition;

    private final Vec3d rotation;

    private final Vec3d scale;

    private double lastResetRotationTick = 0;

    private double lastResetPositionTick = 0;

    private double lastResetScaleTick = 0;

    private boolean rotAnimInProgress = true;

    private boolean posAnimInProgress = true;

    private boolean scaleAnimInProgress = true;

    public AzBoneSnapshot(AzBone bone) {
        this.bone = bone;
        this.offsetPosition = new Vec3d(bone.getPosX(), bone.getPosY(), bone.getPosZ());
        this.rotation = new Vec3d(bone.getRotX(), bone.getRotY(), bone.getRotZ());
        this.scale = new Vec3d(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
    }

    public static AzBoneSnapshot copy(AzBoneSnapshot snapshot) {
        AzBoneSnapshot newSnapshot = new AzBoneSnapshot(snapshot.bone);

        newSnapshot.offsetPosition.set(snapshot.offsetPosition);
        newSnapshot.rotation.set(snapshot.rotation);
        newSnapshot.scale.set(snapshot.scale);

        return newSnapshot;
    }

    public AzBone getBone() {
        return this.bone;
    }

    public float getScaleX() {
        return (float) this.scale.x;
    }

    public float getScaleY() {
        return (float) this.scale.y;
    }

    public float getScaleZ() {
        return (float) this.scale.z;
    }

    public float getOffsetX() {
        return (float) this.offsetPosition.x;
    }

    public float getOffsetY() {
        return (float) this.offsetPosition.y;
    }

    public float getOffsetZ() {
        return (float) this.offsetPosition.z;
    }

    public float getRotX() {
        return (float) this.rotation.x;
    }

    public float getRotY() {
        return (float) this.rotation.y;
    }

    public float getRotZ() {
        return (float) this.rotation.z;
    }

    public double getLastResetRotationTick() {
        return this.lastResetRotationTick;
    }

    public double getLastResetPositionTick() {
        return this.lastResetPositionTick;
    }

    public double getLastResetScaleTick() {
        return this.lastResetScaleTick;
    }

    public boolean isRotAnimInProgress() {
        return this.rotAnimInProgress;
    }

    public boolean isPosAnimInProgress() {
        return this.posAnimInProgress;
    }

    public boolean isScaleAnimInProgress() {
        return this.scaleAnimInProgress;
    }

    /**
     * Update the scale state of this snapshot
     */
    public void updateScale(float scaleX, float scaleY, float scaleZ) {
        scale.set(scaleX, scaleY, scaleZ);
    }

    /**
     * Update the offset state of this snapshot
     */
    public void updateOffset(float offsetX, float offsetY, float offsetZ) {
        offsetPosition.set(offsetX, offsetY, offsetZ);
    }

    /**
     * Update the rotation state of this snapshot
     */
    public void updateRotation(float rotX, float rotY, float rotZ) {
        rotation.set(rotX, rotY, rotZ);
    }

    public void startPosAnim() {
        this.posAnimInProgress = true;
    }

    public void stopPosAnim(double tick) {
        this.posAnimInProgress = false;
        this.lastResetPositionTick = tick;
    }

    public void startRotAnim() {
        this.rotAnimInProgress = true;
    }

    public void stopRotAnim(double tick) {
        this.rotAnimInProgress = false;
        this.lastResetRotationTick = tick;
    }

    public void startScaleAnim() {
        this.scaleAnimInProgress = true;
    }

    public void stopScaleAnim(double tick) {
        this.scaleAnimInProgress = false;
        this.lastResetScaleTick = tick;
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
        return this.bone.getName().hashCode();
    }
}
