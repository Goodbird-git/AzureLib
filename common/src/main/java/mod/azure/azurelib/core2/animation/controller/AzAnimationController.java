package mod.azure.azurelib.core2.animation.controller;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import mod.azure.azurelib.core.animation.EasingType;
import mod.azure.azurelib.core.keyframe.BoneAnimationQueue;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.core.state.BoneSnapshot;
import mod.azure.azurelib.core2.animation.AzAnimationProcessor;
import mod.azure.azurelib.core2.animation.AzAnimationState;
import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzKeyFrameCallbackManager;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzKeyFrameCallbacks;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzKeyFrameProcessor;
import mod.azure.azurelib.core2.animation.primitive.AzAnimation;
import mod.azure.azurelib.core2.animation.primitive.AzQueuedAnimation;
import mod.azure.azurelib.core2.animation.primitive.AzRawAnimation;

/**
 * The actual controller that handles the playing and usage of animations, including their various keyframes and
 * instruction markers. Each controller can only play a single animation at a time - for example you may have one
 * controller to animate walking, one to control attacks, one to control size, etc.
 */
public class AzAnimationController<T> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AzAnimationController.class);

    protected final String name;

    protected final Map<String, BoneAnimationQueue> boneAnimationQueues = new Object2ObjectOpenHashMap<>();

    protected final Map<String, AzRawAnimation> triggerableAnimations = new Object2ObjectOpenHashMap<>(0);

    private final AzAnimationQueue animationQueue;

    private final AzAnimator<T> animator;

    private final AzBoneSnapshotCache boneSnapshotCache;

    private final AzKeyFrameCallbackManager<T> keyFrameCallbackManager;

    private final AzKeyFrameProcessor<T> keyFrameProcessor;

    protected boolean isJustStarting = false;

    protected boolean needsAnimationReload = false;

    protected boolean shouldResetTick = false;

    protected boolean justStartedTransition = false;

    protected AzKeyFrameCallbacks<T> keyFrameCallbacks;

    protected AzRawAnimation triggeredAnimation = null;

    protected double transitionLength;

    protected AzRawAnimation currentRawAnimation;

    protected AzQueuedAnimation currentAnimation;

    protected AzAnimationControllerState animationState = AzAnimationControllerState.STOPPED;

    protected double tickOffset;

    protected ToDoubleFunction<T> animationSpeedModifier = obj -> 1d;

    protected Function<T, EasingType> overrideEasingTypeFunction = obj -> null;

    protected boolean justStopped = true;

    // FIXME: There used to be more constructors here. We should bring those back as a builder pattern.

    /**
     * Instantiates a new {@code AnimationController}.<br>
     *
     * @param name               The name of the controller - should represent what animations it handles
     * @param transitionTickTime The amount of time (in <b>ticks</b>) that the controller should take to transition
     *                           between animations. Lerping is automatically applied where possible
     */
    public AzAnimationController(AzAnimator<T> animator, String name, int transitionTickTime) {
        this.animator = animator;
        this.name = name;
        this.transitionLength = transitionTickTime;
        this.animationQueue = new AzAnimationQueue();
        this.boneSnapshotCache = new AzBoneSnapshotCache();
        this.keyFrameCallbacks = AzKeyFrameCallbacks.noop();
        this.keyFrameCallbackManager = new AzKeyFrameCallbackManager<>(this);
        this.keyFrameProcessor = new AzKeyFrameProcessor<>(this);
    }

    public AzAnimationController<T> setKeyFrameCallbacks(@NotNull AzKeyFrameCallbacks<T> keyFrameCallbacks) {
        Objects.requireNonNull(keyFrameCallbacks);
        this.keyFrameCallbacks = keyFrameCallbacks;
        return this;
    }

    /**
     * Applies the given modifier function to this controller, for handling the speed that the controller should play
     * its animations at.<br>
     * An output value of 1 is considered neutral, with 2 playing an animation twice as fast, 0.5 playing half as fast,
     * etc.
     *
     * @param speedModFunction The function to apply to this controller to handle animation speed
     * @return this
     */
    public AzAnimationController<T> setAnimationSpeedHandler(ToDoubleFunction<T> speedModFunction) {
        this.animationSpeedModifier = speedModFunction;

        return this;
    }

    public void setJustStarting(boolean justStarting) {
        isJustStarting = justStarting;
    }

    /**
     * Sets the controller's {@link EasingType} override for animations.<br>
     * By default, the controller will use whatever {@code EasingType} was defined in the animation json
     *
     * @param easingTypeFunction The new {@code EasingType} to use
     * @return this
     */
    public AzAnimationController<T> setOverrideEasingType(EasingType easingTypeFunction) {
        return setOverrideEasingTypeFunction(obj -> easingTypeFunction);
    }

    public Function<T, EasingType> getOverrideEasingTypeFunction() {
        return overrideEasingTypeFunction;
    }

    /**
     * Sets the controller's {@link EasingType} override function for animations.<br>
     * By default, the controller will use whatever {@code EasingType} was defined in the animation json
     *
     * @param easingType The new {@code EasingType} to use
     * @return this
     */
    public AzAnimationController<T> setOverrideEasingTypeFunction(Function<T, EasingType> easingType) {
        this.overrideEasingTypeFunction = easingType;

        return this;
    }

    /**
     * Registers a triggerable {@link AzRawAnimation} with the controller.<br>
     * These can then be triggered by the various {@code triggerAnim} methods in {@code GeoAnimatable's} subclasses
     *
     * @param name      The name of the triggerable animation
     * @param animation The RawAnimation for this triggerable animation
     * @return this
     */
    public AzAnimationController<T> triggerableAnim(String name, AzRawAnimation animation) {
        this.triggerableAnimations.put(name, animation);

        return this;
    }

    /**
     * Gets the controller's name.
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the currently loaded {@link AzAnimation}. Can be null<br>
     * An animation returned here does not guarantee it is currently playing, just that it is the currently loaded
     * animation for this controller
     */

    public AzQueuedAnimation getCurrentAnimation() {
        return currentAnimation;
    }

    /**
     * Gets the currently loaded animation's {@link BoneAnimationQueue BoneAnimationQueues}.
     */
    public Map<String, BoneAnimationQueue> getBoneAnimationQueues() {
        return boneAnimationQueues;
    }

    /**
     * Gets the current animation speed modifier.<br>
     * This modifier defines the relative speed in which animations will be played based on the current state of the
     * game.
     *
     * @return The computed current animation speed modifier
     */
    public double getAnimationSpeed(T animatable) {
        return animationSpeedModifier.applyAsDouble(animatable);
    }

    /**
     * Applies the given modifier value to this controller, for handlign the speed that the controller hsould play its
     * animations at.<br>
     * A value of 1 is considered neutral, with 2 playing an animation twice as fast, 0.5 playing half as fast, etc.
     *
     * @param speed The speed modifier to apply to this controller to handle animation speed.
     * @return this
     */
    public AzAnimationController<T> setAnimationSpeed(double speed) {
        return setAnimationSpeedHandler(obj -> speed);
    }

    /**
     * Marks the controller as needing to reset its animation and state the next time
     * {@link AzAnimationController#setAnimation(T, AzRawAnimation)} is called.<br>
     * <br>
     * Use this if you have a {@link AzRawAnimation} with multiple stages and you want it to start again from the first
     * stage, or if you want to reset the currently playing animation to the start
     */
    public void forceAnimationReset() {
        this.needsAnimationReload = true;
    }

    /**
     * Tells the controller to stop all animations until told otherwise.<br>
     * Calling this will prevent the controller from continuing to play the currently loaded animation until either
     * {@link AzAnimationController#forceAnimationReset()} is called, or
     * {@link AzAnimationController#setAnimation(T, AzRawAnimation)} is called with a different animation
     */
    public void stop() {
        this.animationState = AzAnimationControllerState.STOPPED;
    }

    /**
     * Overrides the animation transition time for the controller
     */
    public void setTransitionLength(int ticks) {
        this.transitionLength = ticks;
    }

    /**
     * Checks whether the last animation that was playing on this controller has finished or not.<br>
     * This will return true if the controller has had an animation set previously, and it has finished playing and
     * isn't going to loop or proceed to another animation.<br>
     *
     * @return Whether the previous animation finished or not
     */
    public boolean hasAnimationFinished() {
        return currentRawAnimation != null && animationState == AzAnimationControllerState.STOPPED;
    }

    /**
     * Returns the currently cached {@link AzRawAnimation}.<br>
     * This animation may or may not still be playing, but it is the last one to be set in
     * {@link AzAnimationController#setAnimation}
     */
    public AzRawAnimation getCurrentRawAnimation() {
        return currentRawAnimation;
    }

    /**
     * Returns whether the controller is currently playing a triggered animation registered in
     * {@link AzAnimationController#triggerableAnim}<br>
     */
    public boolean isPlayingTriggeredAnimation() {
        return triggeredAnimation != null && !hasAnimationFinished();
    }

    /**
     * Sets the currently loaded animation to the one provided.<br>
     * This method may be safely called every render frame, as passing the same builder that is already loaded will do
     * nothing.<br>
     * Pass null to this method to tell the controller to stop.<br>
     * If {@link AzAnimationController#forceAnimationReset()} has been called prior to this, the controller will reload
     * the animation regardless of whether it matches the currently loaded one or not
     */
    public void setAnimation(T animatable, AzRawAnimation rawAnimation) {
        if (rawAnimation == null || rawAnimation.getAnimationStages().isEmpty()) {
            stop();

            return;
        }

        if (needsAnimationReload || !rawAnimation.equals(currentRawAnimation)) {
            var animations = animator.getAnimationProcessor().buildAnimationQueue(animatable, rawAnimation);

            if (animations != null) {
                animationQueue.clear();
                animationQueue.addAll(animations);
                this.currentRawAnimation = rawAnimation;
                this.shouldResetTick = true;
                this.animationState = AzAnimationControllerState.TRANSITIONING;
                this.justStartedTransition = true;
                this.needsAnimationReload = false;

                return;
            }

            stop();
        }
    }

    /**
     * Attempt to trigger an animation from the list of {@link AzAnimationController#triggerableAnimations triggerable
     * animations} this controller contains.
     *
     * @param animName The name of the animation to trigger
     * @return Whether the controller triggered an animation or not
     */
    public boolean tryTriggerAnimation(String animName) {
        var anim = triggerableAnimations.get(animName);

        if (anim == null) {
            return false;
        }

        this.triggeredAnimation = anim;

        if (animationState == AzAnimationControllerState.STOPPED) {
            this.animationState = AzAnimationControllerState.TRANSITIONING;
            this.shouldResetTick = true;
            this.justStartedTransition = true;
        }

        return true;
    }

    /**
     * Handle a given AnimationState, alongside the current triggered animation if applicable
     */
    protected PlayState handleAnimationState(AzAnimationState<T> state) {
        if (triggeredAnimation != null) {
            if (currentRawAnimation != triggeredAnimation) {
                this.currentAnimation = null;
            }

            setAnimation(state.getAnimatable(), triggeredAnimation);

            if (!hasAnimationFinished()) {
                return PlayState.CONTINUE;
            }

            this.triggeredAnimation = null;
            this.needsAnimationReload = true;
        }

        // TODO: Revisit this.
        return PlayState.CONTINUE;
    }

    /**
     * This method is called every frame in order to populate the animation point queues, and process animation state
     * logic.
     *
     * @param state                 The animation test state
     * @param bones                 The registered {@link CoreGeoBone bones} for this model
     * @param snapshots             The {@link BoneSnapshot} map
     * @param seekTime              The current tick + partial tick
     * @param crashWhenCantFindBone Whether to hard-fail when a bone can't be found, or to continue with the remaining
     *                              bones
     */
    public void process(
        AzAnimationState<T> state,
        Map<String, CoreGeoBone> bones,
        Map<String, BoneSnapshot> snapshots,
        final double seekTime,
        boolean crashWhenCantFindBone
    ) {
        var animatable = state.getAnimatable();
        double adjustedTick = adjustTick(animatable, seekTime);

        if (animationState == AzAnimationControllerState.TRANSITIONING && adjustedTick >= transitionLength) {
            this.shouldResetTick = true;
            this.animationState = AzAnimationControllerState.RUNNING;
            adjustedTick = adjustTick(animatable, seekTime);
        }

        PlayState playState = handleAnimationState(state);

        if (playState == PlayState.STOP || (currentAnimation == null && animationQueue.isEmpty())) {
            this.animationState = AzAnimationControllerState.STOPPED;
            this.justStopped = true;

            return;
        }

        createInitialQueues(bones.values());

        if (justStartedTransition && (shouldResetTick || justStopped)) {
            this.justStopped = false;
            adjustedTick = adjustTick(animatable, seekTime);

            if (currentAnimation == null) {
                this.animationState = AzAnimationControllerState.TRANSITIONING;
            }
        } else if (currentAnimation == null) {
            this.shouldResetTick = true;
            this.animationState = AzAnimationControllerState.TRANSITIONING;
            this.justStartedTransition = true;
            this.needsAnimationReload = false;
            adjustedTick = adjustTick(animatable, seekTime);
        } else if (animationState != AzAnimationControllerState.TRANSITIONING) {
            this.animationState = AzAnimationControllerState.RUNNING;
        }

        if (getAnimationState() == AzAnimationControllerState.RUNNING) {
            keyFrameProcessor.runCurrentAnimation(
                boneAnimationQueues,
                animatable,
                adjustedTick,
                seekTime,
                crashWhenCantFindBone
            );
            var canTransition = transitionLength == 0 && shouldResetTick;

            if (canTransition && animationState == AzAnimationControllerState.TRANSITIONING) {
                this.currentAnimation = this.animationQueue.next();
            }
        } else if (animationState == AzAnimationControllerState.TRANSITIONING) {
            if (adjustedTick == 0 || isJustStarting) {
                this.justStartedTransition = false;
                this.currentAnimation = animationQueue.next();

                keyFrameCallbackManager.reset();

                if (currentAnimation == null) {
                    return;
                }

                boneSnapshotCache.put(currentAnimation, snapshots.values());
            }

            if (currentAnimation != null) {
                keyFrameProcessor.transitionFromCurrentAnimation(
                    boneAnimationQueues,
                    bones,
                    crashWhenCantFindBone,
                    adjustedTick
                );
            }
        }
    }

    /**
     * Prepare the {@link BoneAnimationQueue} map for the current render frame
     *
     * @param modelRendererList The bone list from the {@link AzAnimationProcessor}
     */
    protected void createInitialQueues(Collection<CoreGeoBone> modelRendererList) {
        boneAnimationQueues.clear();

        for (var modelRenderer : modelRendererList) {
            boneAnimationQueues.put(modelRenderer.getName(), new BoneAnimationQueue(modelRenderer));
        }
    }

    /**
     * Adjust a tick value depending on the controller's current state and speed modifier.<br>
     * Is used when starting a new animation, transitioning, and a few other key areas
     *
     * @param tick The currently used tick value
     * @return 0 if {@link AzAnimationController#shouldResetTick} is set to false, or a
     *         {@link AzAnimationController#animationSpeedModifier} modified value otherwise
     */
    public double adjustTick(T animatable, double tick) {
        if (!shouldResetTick) {
            return animationSpeedModifier.applyAsDouble(animatable) * Math.max(tick - tickOffset, 0);
        }

        if (getAnimationState() != AzAnimationControllerState.STOPPED) {
            this.tickOffset = tick;
        }

        this.shouldResetTick = false;

        return 0;
    }

    /**
     * Returns the current state of this controller.
     */
    public AzAnimationControllerState getAnimationState() {
        return animationState;
    }

    public void setAnimationState(AzAnimationControllerState animationState) {
        this.animationState = animationState;
    }

    public AzAnimationQueue getAnimationQueue() {
        return animationQueue;
    }

    public AzBoneSnapshotCache getBoneSnapshotCache() {
        return boneSnapshotCache;
    }

    public AzKeyFrameCallbacks<T> getKeyFrameCallbacks() {
        return keyFrameCallbacks;
    }

    public AzKeyFrameCallbackManager<T> getKeyFrameCallbackManager() {
        return keyFrameCallbackManager;
    }

    public double getTransitionLength() {
        return transitionLength;
    }

    public boolean shouldResetTick() {
        return shouldResetTick;
    }

    public void setShouldResetTick(boolean shouldResetTick) {
        this.shouldResetTick = shouldResetTick;
    }

    public void setCurrentAnimation(AzQueuedAnimation currentAnimation) {
        this.currentAnimation = currentAnimation;
    }
}
