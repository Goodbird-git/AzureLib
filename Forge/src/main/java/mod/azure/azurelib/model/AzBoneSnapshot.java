package mod.azure.azurelib.model;

import net.minecraft.util.math.Vec3d;

/**
 * A state monitoring class for a given {@link AzBone}.<br>
 */
public class AzBoneSnapshot {

    private final AzBone bone;

    private float scaleValueX;
    private float scaleValueY;
    private float scaleValueZ;

    private float positionOffsetX;
    private float positionOffsetY;
    private float positionOffsetZ;

    private float rotationValueX;
    private float rotationValueY;
    private float rotationValueZ;

    private double lastResetRotationTick = 0;

    private double lastResetPositionTick = 0;

    private double lastResetScaleTick = 0;

    private boolean rotAnimInProgress = true;

    private boolean posAnimInProgress = true;

    private boolean scaleAnimInProgress = true;

    public AzBoneSnapshot(AzBone bone) {
        this.bone = bone;
        this.scaleValueX = bone.getScaleX();
        this.scaleValueY = bone.getScaleY();
        this.scaleValueZ = bone.getScaleZ();
        this.positionOffsetX = bone.getPosX();
        this.positionOffsetY = bone.getPosY();
        this.positionOffsetZ = bone.getPosZ();
        this.rotationValueX = bone.getRotX();
        this.rotationValueY = bone.getRotY();
        this.rotationValueZ = bone.getRotZ();
    }

    public static AzBoneSnapshot copy(AzBoneSnapshot snapshot) {
        AzBoneSnapshot newSnapshot = new AzBoneSnapshot(snapshot.bone);

        newSnapshot.scaleValueX = snapshot.scaleValueX;
        newSnapshot.scaleValueY = snapshot.scaleValueY;
        newSnapshot.scaleValueZ = snapshot.scaleValueZ;
        newSnapshot.positionOffsetX = snapshot.positionOffsetX;
        newSnapshot.positionOffsetY = snapshot.positionOffsetY;
        newSnapshot.positionOffsetZ = snapshot.positionOffsetZ;
        newSnapshot.rotationValueX = snapshot.rotationValueX;
        newSnapshot.rotationValueY = snapshot.rotationValueY;
        newSnapshot.rotationValueZ = snapshot.rotationValueZ;

        return newSnapshot;
    }

    public AzBone getBone() {
        return this.bone;
    }

    public float getScaleX() {
        return this.scaleValueX;
    }

    public float getScaleY() {
        return this.scaleValueY;
    }

    public float getScaleZ() {
        return this.scaleValueZ;
    }

    public float getOffsetX() {
        return this.positionOffsetX;
    }

    public float getOffsetY() {
        return this.positionOffsetY;
    }

    public float getOffsetZ() {
        return this.positionOffsetZ;
    }

    public float getRotX() {
        return this.rotationValueX;
    }

    public float getRotY() {
        return this.rotationValueY;
    }

    public float getRotZ() {
        return this.rotationValueZ;
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
        this.scaleValueX = scaleX;
        this.scaleValueY = scaleY;
        this.scaleValueZ = scaleZ;
    }

    /**
     * Update the offset state of this snapshot
     */
    public void updateOffset(float offsetX, float offsetY, float offsetZ) {
        this.positionOffsetX = offsetX;
        this.positionOffsetY = offsetY;
        this.positionOffsetZ = offsetZ;
    }

    /**
     * Update the rotation state of this snapshot
     */
    public void updateRotation(float rotX, float rotY, float rotZ) {
        this.rotationValueX = rotX;
        this.rotationValueY = rotY;
        this.rotationValueZ = rotZ;
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
