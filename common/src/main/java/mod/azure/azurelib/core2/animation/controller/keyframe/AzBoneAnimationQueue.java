/**
 * This class is a fork of the matching class found in the Geckolib repository. Original source:
 * https://github.com/bernie-g/geckolib Copyright © 2024 Bernie-G. Licensed under the MIT License.
 * https://github.com/bernie-g/geckolib/blob/main/LICENSE
 */
package mod.azure.azurelib.core2.animation.controller.keyframe;

import mod.azure.azurelib.core2.animation.AzKeyframe;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.model.AzBone;
import mod.azure.azurelib.core2.model.AzBoneSnapshot;

/**
 * A bone pseudo-stack for bone animation positions, scales, and rotations. Animation points are calculated then pushed
 * onto their respective queues to be used for transformations in rendering
 */
public class AzBoneAnimationQueue {

    private AzBone bone;

    private AzAnimationPoint rotationXQueue;

    private AzAnimationPoint rotationYQueue;

    private AzAnimationPoint rotationZQueue;

    private AzAnimationPoint positionXQueue;

    private AzAnimationPoint positionYQueue;

    private AzAnimationPoint positionZQueue;

    private AzAnimationPoint scaleXQueue;

    private AzAnimationPoint scaleYQueue;

    private AzAnimationPoint scaleZQueue;

    public AzBoneAnimationQueue(AzBone bone) {
        this.bone = bone;
        this.rotationXQueue = AzAnimationPoint.create();
        this.rotationYQueue = AzAnimationPoint.create();
        this.rotationZQueue = AzAnimationPoint.create();
        this.positionXQueue = AzAnimationPoint.create();
        this.positionYQueue = AzAnimationPoint.create();
        this.positionZQueue = AzAnimationPoint.create();
        this.scaleXQueue = AzAnimationPoint.create();
        this.scaleYQueue = AzAnimationPoint.create();
        this.scaleZQueue = AzAnimationPoint.create();
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#positionXQueue}
     *
     * @param keyFrame         The {@code Nullable} Keyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addPosXPoint(
        AzKeyframe<?> keyFrame,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.positionXQueue.set(keyFrame, lerpedTick, transitionLength, startValue, endValue);
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#positionYQueue}
     *
     * @param keyFrame         The {@code Nullable} Keyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addPosYPoint(
        AzKeyframe<?> keyFrame,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.positionYQueue.set(keyFrame, lerpedTick, transitionLength, startValue, endValue);
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#positionZQueue}
     *
     * @param keyFrame         The {@code Nullable} Keyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addPosZPoint(
        AzKeyframe<?> keyFrame,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.positionZQueue.set(keyFrame, lerpedTick, transitionLength, startValue, endValue);
    }

    /**
     * Add a new X, Y, and Z position {@link AzAnimationPoint} to their respective queues
     *
     * @param keyFrame         The {@code Nullable} Keyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (base on the {@link AzAnimationController}
     * @param startSnapshot    The {@link AzBoneSnapshot} that serves as the starting positions relevant to the keyframe
     *                         provided
     * @param nextXPoint       The X {@code AnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     * @param nextYPoint       The Y {@code AnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     * @param nextZPoint       The Z {@code AnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     */
    public void addNextPosition(
        AzKeyframe<?> keyFrame,
        double lerpedTick,
        double transitionLength,
        AzBoneSnapshot startSnapshot,
        AzAnimationPoint nextXPoint,
        AzAnimationPoint nextYPoint,
        AzAnimationPoint nextZPoint
    ) {
        addPosXPoint(
            keyFrame,
            lerpedTick,
            transitionLength,
            startSnapshot.getOffsetX(),
            nextXPoint.animationStartValue()
        );
        addPosYPoint(
            keyFrame,
            lerpedTick,
            transitionLength,
            startSnapshot.getOffsetY(),
            nextYPoint.animationStartValue()
        );
        addPosZPoint(
            keyFrame,
            lerpedTick,
            transitionLength,
            startSnapshot.getOffsetZ(),
            nextZPoint.animationStartValue()
        );
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#scaleXQueue}
     *
     * @param keyFrame         The {@code Nullable} Keyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addScaleXPoint(
        AzKeyframe<?> keyFrame,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.scaleXQueue.set(keyFrame, lerpedTick, transitionLength, startValue, endValue);
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#scaleYQueue}
     *
     * @param keyFrame         The {@code Nullable} Keyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addScaleYPoint(
        AzKeyframe<?> keyFrame,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.scaleYQueue.set(keyFrame, lerpedTick, transitionLength, startValue, endValue);
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#scaleZQueue}
     *
     * @param keyFrame         The {@code Nullable} Keyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addScaleZPoint(
        AzKeyframe<?> keyFrame,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.scaleZQueue.set(keyFrame, lerpedTick, transitionLength, startValue, endValue);
    }

    /**
     * Add a new X, Y, and Z scale {@link AzAnimationPoint} to their respective queues
     *
     * @param keyFrame         The {@code Nullable} Keyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (base on the {@link AzAnimationController}
     * @param startSnapshot    The {@link AzBoneSnapshot} that serves as the starting scales relevant to the keyframe
     *                         provided
     * @param nextXPoint       The X {@code AnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     * @param nextYPoint       The Y {@code AnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     * @param nextZPoint       The Z {@code AnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     */
    public void addNextScale(
        AzKeyframe<?> keyFrame,
        double lerpedTick,
        double transitionLength,
        AzBoneSnapshot startSnapshot,
        AzAnimationPoint nextXPoint,
        AzAnimationPoint nextYPoint,
        AzAnimationPoint nextZPoint
    ) {
        addScaleXPoint(
            keyFrame,
            lerpedTick,
            transitionLength,
            startSnapshot.getScaleX(),
            nextXPoint.animationStartValue()
        );
        addScaleYPoint(
            keyFrame,
            lerpedTick,
            transitionLength,
            startSnapshot.getScaleY(),
            nextYPoint.animationStartValue()
        );
        addScaleZPoint(
            keyFrame,
            lerpedTick,
            transitionLength,
            startSnapshot.getScaleZ(),
            nextZPoint.animationStartValue()
        );
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#rotationXQueue}
     *
     * @param keyFrame         The {@code Nullable} Keyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addRotationXPoint(
        AzKeyframe<?> keyFrame,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.rotationXQueue.set(keyFrame, lerpedTick, transitionLength, startValue, endValue);
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#rotationYQueue}
     *
     * @param keyFrame         The {@code Nullable} Keyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addRotationYPoint(
        AzKeyframe<?> keyFrame,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.rotationYQueue.set(keyFrame, lerpedTick, transitionLength, startValue, endValue);
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#rotationZQueue}
     *
     * @param keyFrame         The {@code Nullable} Keyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addRotationZPoint(
        AzKeyframe<?> keyFrame,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.rotationZQueue.set(keyFrame, lerpedTick, transitionLength, startValue, endValue);
    }

    /**
     * Add a new X, Y, and Z scale {@link AzAnimationPoint} to their respective queues
     *
     * @param keyFrame         The {@code Nullable} Keyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (base on the {@link AzAnimationController}
     * @param startSnapshot    The {@link AzBoneSnapshot} that serves as the starting rotations relevant to the keyframe
     *                         provided
     * @param initialSnapshot  The {@link AzBoneSnapshot} that serves as the unmodified rotations of the bone
     * @param nextXPoint       The X {@code AnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     * @param nextYPoint       The Y {@code AnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     * @param nextZPoint       The Z {@code AnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     */
    public void addNextRotation(
        AzKeyframe<?> keyFrame,
        double lerpedTick,
        double transitionLength,
        AzBoneSnapshot startSnapshot,
        AzBoneSnapshot initialSnapshot,
        AzAnimationPoint nextXPoint,
        AzAnimationPoint nextYPoint,
        AzAnimationPoint nextZPoint
    ) {
        addRotationXPoint(
            keyFrame,
            lerpedTick,
            transitionLength,
            startSnapshot.getRotX() - initialSnapshot.getRotX(),
            nextXPoint.animationStartValue()
        );
        addRotationYPoint(
            keyFrame,
            lerpedTick,
            transitionLength,
            startSnapshot.getRotY() - initialSnapshot.getRotY(),
            nextYPoint.animationStartValue()
        );
        addRotationZPoint(
            keyFrame,
            lerpedTick,
            transitionLength,
            startSnapshot.getRotZ() - initialSnapshot.getRotZ(),
            nextZPoint.animationStartValue()
        );
    }

    /**
     * Add an X, Y, and Z position {@link AzAnimationPoint} to their respective queues
     *
     * @param xPoint The x position {@code AzAnimationPoint} to add
     * @param yPoint The y position {@code AzAnimationPoint} to add
     * @param zPoint The z position {@code AzAnimationPoint} to add
     */
    public void addPositions(AzAnimationPoint xPoint, AzAnimationPoint yPoint, AzAnimationPoint zPoint) {
        this.positionXQueue = xPoint;
        this.positionYQueue = yPoint;
        this.positionZQueue = zPoint;
    }

    /**
     * Add an X, Y, and Z scale {@link AzAnimationPoint} to their respective queues
     *
     * @param xPoint The x scale {@code AzAnimationPoint} to add
     * @param yPoint The y scale {@code AzAnimationPoint} to add
     * @param zPoint The z scale {@code AzAnimationPoint} to add
     */
    public void addScales(AzAnimationPoint xPoint, AzAnimationPoint yPoint, AzAnimationPoint zPoint) {
        this.scaleXQueue = xPoint;
        this.scaleYQueue = yPoint;
        this.scaleZQueue = zPoint;
    }

    /**
     * Add an X, Y, and Z rotation {@link AzAnimationPoint} to their respective queues
     *
     * @param xPoint The x rotation {@code AzAnimationPoint} to add
     * @param yPoint The y rotation {@code AzAnimationPoint} to add
     * @param zPoint The z rotation {@code AzAnimationPoint} to add
     */
    public void addRotations(AzAnimationPoint xPoint, AzAnimationPoint yPoint, AzAnimationPoint zPoint) {
        this.rotationXQueue = xPoint;
        this.rotationYQueue = yPoint;
        this.rotationZQueue = zPoint;
    }

    public AzBone bone() {
        return bone;
    }

    public AzAnimationPoint rotationXQueue() {
        return rotationXQueue;
    }

    public AzAnimationPoint rotationYQueue() {
        return rotationYQueue;
    }

    public AzAnimationPoint rotationZQueue() {
        return rotationZQueue;
    }

    public AzAnimationPoint positionXQueue() {
        return positionXQueue;
    }

    public AzAnimationPoint positionYQueue() {
        return positionYQueue;
    }

    public AzAnimationPoint positionZQueue() {
        return positionZQueue;
    }

    public AzAnimationPoint scaleXQueue() {
        return scaleXQueue;
    }

    public AzAnimationPoint scaleYQueue() {
        return scaleYQueue;
    }

    public AzAnimationPoint scaleZQueue() {
        return scaleZQueue;
    }
}
