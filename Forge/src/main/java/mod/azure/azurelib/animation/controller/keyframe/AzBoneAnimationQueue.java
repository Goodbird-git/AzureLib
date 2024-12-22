package mod.azure.azurelib.animation.controller.keyframe;

import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.model.AzBone;
import mod.azure.azurelib.model.AzBoneSnapshot;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A bone pseudo-stack for bone animation positions, scales, and rotations. Animation points are calculated then pushed
 * onto their respective queues to be used for transformations in rendering
 */
public class AzBoneAnimationQueue {
    public AzBone bone;
    public Queue<AzAnimationPoint> rotationXQueue;
    public Queue<AzAnimationPoint> rotationYQueue;
    public Queue<AzAnimationPoint> rotationZQueue;
    public Queue<AzAnimationPoint> positionXQueue;
    public Queue<AzAnimationPoint> positionYQueue;
    public Queue<AzAnimationPoint> positionZQueue;
    public Queue<AzAnimationPoint> scaleXQueue;
    public Queue<AzAnimationPoint> scaleYQueue;
    public Queue<AzAnimationPoint> scaleZQueue;

    public AzBoneAnimationQueue(AzBone bone, Queue<AzAnimationPoint> rotationXQueue, Queue<AzAnimationPoint> rotationYQueue, Queue<AzAnimationPoint> rotationZQueue, Queue<AzAnimationPoint> positionXQueue, Queue<AzAnimationPoint> positionYQueue, Queue<AzAnimationPoint> positionZQueue, Queue<AzAnimationPoint> scaleXQueue, Queue<AzAnimationPoint> scaleYQueue, Queue<AzAnimationPoint> scaleZQueue) {
        this.rotationXQueue = rotationXQueue;
        this.rotationYQueue = rotationYQueue;
        this.rotationZQueue = rotationZQueue;
        this.positionXQueue = positionXQueue;
        this.positionYQueue = positionYQueue;
        this.positionZQueue = positionZQueue;
        this.scaleXQueue = scaleXQueue;
        this.scaleYQueue = scaleYQueue;
        this.scaleZQueue = scaleZQueue;
        this.bone = bone;
    }

    public AzBone bone() {
        return bone;
    }

    public Queue<AzAnimationPoint> rotationXQueue() {
        return rotationXQueue;
    }

    public Queue<AzAnimationPoint> rotationYQueue() {
        return rotationYQueue;
    }

    public Queue<AzAnimationPoint> rotationZQueue() {
        return rotationZQueue;
    }

    public Queue<AzAnimationPoint> positionXQueue() {
        return positionXQueue;
    }

    public Queue<AzAnimationPoint> positionYQueue() {
        return positionYQueue;
    }

    public Queue<AzAnimationPoint> positionZQueue() {
        return positionZQueue;
    }

    public Queue<AzAnimationPoint> scaleXQueue() {
        return scaleXQueue;
    }

    public Queue<AzAnimationPoint> scaleYQueue() {
        return scaleYQueue;
    }

    public Queue<AzAnimationPoint> scaleZQueue() {
        return scaleZQueue;
    }

    public AzBoneAnimationQueue(AzBone bone) {
        // TODO: Optimize
        this(
            bone,
            new LinkedList<>(),
            new LinkedList<>(),
            new LinkedList<>(),
            new LinkedList<>(),
            new LinkedList<>(),
            new LinkedList<>(),
            new LinkedList<>(),
            new LinkedList<>(),
            new LinkedList<>()
        );
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#positionXQueue}
     *
     * @param keyframe         The {@code Nullable} Keyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addPosXPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.positionXQueue.add(new AzAnimationPoint(keyframe, lerpedTick, transitionLength, startValue, endValue));
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#positionYQueue}
     *
     * @param keyframe         The {@code Nullable} AzKeyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addPosYPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.positionYQueue.add(new AzAnimationPoint(keyframe, lerpedTick, transitionLength, startValue, endValue));
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#positionZQueue}
     *
     * @param keyframe         The {@code Nullable} AzKeyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addPosZPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.positionZQueue.add(new AzAnimationPoint(keyframe, lerpedTick, transitionLength, startValue, endValue));
    }

    /**
     * Add a new X, Y, and Z position {@link AzAnimationPoint} to their respective queues
     *
     * @param keyframe         The {@code Nullable} AzKeyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (base on the {@link AzAnimationController}
     * @param startSnapshot    The {@link AzBoneSnapshot} that serves as the starting positions relevant to the keyframe
     *                         provided
     * @param nextXPoint       The X {@code AzAnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     * @param nextYPoint       The Y {@code AzAnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     * @param nextZPoint       The Z {@code AzAnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     */
    public void addNextPosition(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        AzBoneSnapshot startSnapshot,
        AzAnimationPoint nextXPoint,
        AzAnimationPoint nextYPoint,
        AzAnimationPoint nextZPoint
    ) {
        addPosXPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getOffsetX(),
            nextXPoint.animationStartValue()
        );
        addPosYPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getOffsetY(),
            nextYPoint.animationStartValue()
        );
        addPosZPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getOffsetZ(),
            nextZPoint.animationStartValue()
        );
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#scaleXQueue}
     *
     * @param keyframe         The {@code Nullable} AzKeyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addScaleXPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.scaleXQueue.add(new AzAnimationPoint(keyframe, lerpedTick, transitionLength, startValue, endValue));
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#scaleYQueue}
     *
     * @param keyframe         The {@code Nullable} AzKeyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addScaleYPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.scaleYQueue.add(new AzAnimationPoint(keyframe, lerpedTick, transitionLength, startValue, endValue));
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#scaleZQueue}
     *
     * @param keyframe         The {@code Nullable} AzKeyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addScaleZPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.scaleZQueue.add(new AzAnimationPoint(keyframe, lerpedTick, transitionLength, startValue, endValue));
    }

    /**
     * Add a new X, Y, and Z scale {@link AzAnimationPoint} to their respective queues
     *
     * @param keyframe         The {@code Nullable} AzKeyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (base on the {@link AzAnimationController}
     * @param startSnapshot    The {@link AzBoneSnapshot} that serves as the starting scales relevant to the keyframe
     *                         provided
     * @param nextXPoint       The X {@code AzAnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     * @param nextYPoint       The Y {@code AzAnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     * @param nextZPoint       The Z {@code AzAnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     */
    public void addNextScale(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        AzBoneSnapshot startSnapshot,
        AzAnimationPoint nextXPoint,
        AzAnimationPoint nextYPoint,
        AzAnimationPoint nextZPoint
    ) {
        addScaleXPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getScaleX(),
            nextXPoint.animationStartValue()
        );
        addScaleYPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getScaleY(),
            nextYPoint.animationStartValue()
        );
        addScaleZPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getScaleZ(),
            nextZPoint.animationStartValue()
        );
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#rotationXQueue}
     *
     * @param keyframe         The {@code Nullable} AzKeyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addRotationXPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.rotationXQueue.add(new AzAnimationPoint(keyframe, lerpedTick, transitionLength, startValue, endValue));
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#rotationYQueue}
     *
     * @param keyframe         The {@code Nullable} AzKeyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addRotationYPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.rotationYQueue.add(new AzAnimationPoint(keyframe, lerpedTick, transitionLength, startValue, endValue));
    }

    /**
     * Add a new {@link AzAnimationPoint} to the {@link AzBoneAnimationQueue#rotationZQueue}
     *
     * @param keyframe         The {@code Nullable} AzKeyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (based on the {@link AzAnimationController})
     * @param startValue       The value of the point at the start of its transition
     * @param endValue         The value of the point at the end of its transition
     */
    public void addRotationZPoint(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        double startValue,
        double endValue
    ) {
        this.rotationZQueue.add(new AzAnimationPoint(keyframe, lerpedTick, transitionLength, startValue, endValue));
    }

    /**
     * Add a new X, Y, and Z scale {@link AzAnimationPoint} to their respective queues
     *
     * @param keyframe         The {@code Nullable} AzKeyframe relevant to the animation point
     * @param lerpedTick       The lerped time (current tick + partial tick) that the point starts at
     * @param transitionLength The length of the transition (base on the {@link AzAnimationController}
     * @param startSnapshot    The {@link AzBoneSnapshot} that serves as the starting rotations relevant to the keyframe
     *                         provided
     * @param initialSnapshot  The {@link AzBoneSnapshot} that serves as the unmodified rotations of the bone
     * @param nextXPoint       The X {@code AzAnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     * @param nextYPoint       The Y {@code AzAnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     * @param nextZPoint       The Z {@code AzAnimationPoint} that is next in the queue, to serve as the end value of the
     *                         new point
     */
    public void addNextRotation(
        AzKeyframe<?> keyframe,
        double lerpedTick,
        double transitionLength,
        AzBoneSnapshot startSnapshot,
        AzBoneSnapshot initialSnapshot,
        AzAnimationPoint nextXPoint,
        AzAnimationPoint nextYPoint,
        AzAnimationPoint nextZPoint
    ) {
        addRotationXPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getRotX() - initialSnapshot.getRotX(),
            nextXPoint.animationStartValue()
        );
        addRotationYPoint(
            keyframe,
            lerpedTick,
            transitionLength,
            startSnapshot.getRotY() - initialSnapshot.getRotY(),
            nextYPoint.animationStartValue()
        );
        addRotationZPoint(
            keyframe,
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
        this.positionXQueue.add(xPoint);
        this.positionYQueue.add(yPoint);
        this.positionZQueue.add(zPoint);
    }

    /**
     * Add an X, Y, and Z scale {@link AzAnimationPoint} to their respective queues
     *
     * @param xPoint The x scale {@code AzAnimationPoint} to add
     * @param yPoint The y scale {@code AzAnimationPoint} to add
     * @param zPoint The z scale {@code AzAnimationPoint} to add
     */
    public void addScales(AzAnimationPoint xPoint, AzAnimationPoint yPoint, AzAnimationPoint zPoint) {
        this.scaleXQueue.add(xPoint);
        this.scaleYQueue.add(yPoint);
        this.scaleZQueue.add(zPoint);
    }

    /**
     * Add an X, Y, and Z rotation {@link AzAnimationPoint} to their respective queues
     *
     * @param xPoint The x rotation {@code AzAnimationPoint} to add
     * @param yPoint The y rotation {@code AzAnimationPoint} to add
     * @param zPoint The z rotation {@code AzAnimationPoint} to add
     */
    public void addRotations(AzAnimationPoint xPoint, AzAnimationPoint yPoint, AzAnimationPoint zPoint) {
        this.rotationXQueue.add(xPoint);
        this.rotationYQueue.add(yPoint);
        this.rotationZQueue.add(zPoint);
    }
}
