package mod.azure.azurelib.core2.animation.controller;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import mod.azure.azurelib.core.animation.EasingType;
import mod.azure.azurelib.core2.animation.AzAnimationContext;
import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzBoneAnimationQueue;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzKeyFrameCallbackManager;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzKeyFrameCallbacks;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzKeyFrameProcessor;
import mod.azure.azurelib.core2.animation.controller.state.impl.AzAnimationPauseState;
import mod.azure.azurelib.core2.animation.controller.state.impl.AzAnimationPlayState;
import mod.azure.azurelib.core2.animation.controller.state.impl.AzAnimationStopState;
import mod.azure.azurelib.core2.animation.controller.state.impl.AzAnimationTransitionState;
import mod.azure.azurelib.core2.animation.controller.state.machine.AzAnimationControllerStateMachine;
import mod.azure.azurelib.core2.animation.primitive.AzAnimation;
import mod.azure.azurelib.core2.animation.primitive.AzQueuedAnimation;
import mod.azure.azurelib.core2.animation.primitive.AzRawAnimation;
import mod.azure.azurelib.core2.animation.primitive.AzStage;

/**
 * The actual controller that handles the playing and usage of animations, including their various keyframes and
 * instruction markers. Each controller can only play a single animation at a time - for example you may have one
 * controller to animate walking, one to control attacks, one to control size, etc.
 */
public class AzAnimationController<T> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AzAnimationController.class);

    private final String name;

    private final Map<String, AzRawAnimation> triggerableAnimations = new Object2ObjectOpenHashMap<>(0);

    private final AzAnimationQueue animationQueue;

    private final AzAnimationControllerStateMachine<T> stateMachine;

    private final AzAnimator<T> animator;

    private final AzBoneAnimationQueueCache boneAnimationQueueCache;

    private final AzBoneSnapshotCache boneSnapshotCache;

    private final AzKeyFrameCallbackManager<T> keyFrameCallbackManager;

    private final AzKeyFrameProcessor<T> keyFrameProcessor;

    protected boolean needsAnimationReload = false;

    protected AzKeyFrameCallbacks<T> keyFrameCallbacks;

    protected AzRawAnimation triggeredAnimation = null;

    protected double transitionLength;

    protected AzRawAnimation currentRawAnimation;

    protected AzQueuedAnimation currentAnimation;

    protected double tickOffset;

    protected ToDoubleFunction<T> animationSpeedModifier = obj -> 1d;

    protected Function<T, EasingType> overrideEasingTypeFunction = obj -> null;

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
        this.boneAnimationQueueCache = new AzBoneAnimationQueueCache(animator.context().boneCache());
        this.boneSnapshotCache = new AzBoneSnapshotCache();
        this.keyFrameCallbacks = AzKeyFrameCallbacks.noop();
        this.keyFrameCallbackManager = new AzKeyFrameCallbackManager<>(this);
        this.keyFrameProcessor = new AzKeyFrameProcessor<>(this, boneAnimationQueueCache);

        var stateHolder = new AzAnimationControllerStateMachine.StateHolder<T>(
            new AzAnimationPlayState<>(),
            new AzAnimationPauseState<>(),
            new AzAnimationStopState<>(),
            new AzAnimationTransitionState<>()
        );

        this.stateMachine = new AzAnimationControllerStateMachine<>(stateHolder, this, animator.context());
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

    public String getName() {
        return name;
    }

    public @Nullable AzQueuedAnimation getCurrentAnimation() {
        return currentAnimation;
    }

    public Collection<AzBoneAnimationQueue> getBoneAnimationQueues() {
        return boneAnimationQueueCache.values();
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
     * Checks whether the last animation that was playing on this controller has finished or not.<br>
     * This will return true if the controller has had an animation set previously, and it has finished playing and
     * isn't going to loop or proceed to another animation.<br>
     *
     * @return Whether the previous animation finished or not
     */
    public boolean hasAnimationFinished() {
        return currentRawAnimation != null && stateMachine.isStopped();
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
     * Populates the animation queue with the given {@link AzRawAnimation}
     *
     * @param animatable   The animatable object being rendered
     * @param rawAnimation The raw animation to be compiled
     * @return Whether the animations were loaded into the queue.
     */
    public List<AzQueuedAnimation> tryCreateAnimationQueue(T animatable, AzRawAnimation rawAnimation) {
        var stages = rawAnimation.getAnimationStages();
        var animations = new ArrayList<AzQueuedAnimation>();

        for (var stage : stages) {
            var animation = Objects.equals(stage.animationName(), AzStage.WAIT)
                ? AzAnimation.generateWaitAnimation(stage.additionalTicks())
                : animator.getAnimation(animatable, stage.animationName());

            if (animation == null) {
                LOGGER.warn(
                    "Unable to find animation: {} for {}",
                    stage.animationName(),
                    animatable.getClass().getSimpleName()
                );
                return List.of();
            } else {
                animations.add(new AzQueuedAnimation(animation, stage.loopType()));
            }
        }

        return animations;
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
            stateMachine.stop();

            return;
        }

        if (needsAnimationReload || !rawAnimation.equals(currentRawAnimation)) {
            var animations = tryCreateAnimationQueue(animatable, rawAnimation);

            if (!animations.isEmpty()) {
                animationQueue.clear();
                animationQueue.addAll(animations);
                this.currentRawAnimation = rawAnimation;
                stateMachine.transition();
                this.needsAnimationReload = false;

                return;
            }

            stateMachine.stop();
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

        if (stateMachine.isStopped()) {
            stateMachine.transition();
        }

        return true;
    }

    /**
     * Handle a given AnimationState, alongside the current triggered animation if applicable
     */
    private void handleAnimationState(T animatable) {
        if (triggeredAnimation != null) {
            if (currentRawAnimation != triggeredAnimation) {
                this.currentAnimation = null;
            }

            setAnimation(animatable, triggeredAnimation);

            if (!hasAnimationFinished()) {
                return;
            }

            this.triggeredAnimation = null;
            this.needsAnimationReload = true;
        }
    }

    /**
     * This method is called every frame in order to populate the animation point queues, and process animation state
     * logic.
     */
    public void update(AzAnimationContext<T> context) {
        var animatable = context.animatable();
        var timer = context.timer();
        var seekTime = timer.getAnimTime();

        handleAnimationState(animatable);

        // Adjust the tick before making any updates.
        stateMachine.getContext().adjustedTick = adjustTick(animatable, seekTime);
        // Run state machine updates.
        stateMachine.update();

        if (currentAnimation == null) {
            if (animationQueue.isEmpty()) {
                // If there is no animation to play, stop.
                stateMachine.stop();
                return;
            }

            this.needsAnimationReload = false;
            stateMachine.getContext().adjustedTick = adjustTick(animatable, seekTime);
        }
    }

    /**
     * Adjust a tick value depending on the controller's current state and speed modifier.<br>
     * Is used when starting a new animation, transitioning, and a few other key areas
     *
     * @param tick The currently used tick value
     * @return 0 if {@link AzAnimationControllerStateMachine#shouldResetTick()} is set to false, or a
     *         {@link AzAnimationController#animationSpeedModifier} modified value otherwise
     */
    public double adjustTick(T animatable, double tick) {
        if (!stateMachine.shouldResetTick()) {
            return animationSpeedModifier.applyAsDouble(animatable) * Math.max(tick - tickOffset, 0);
        }

        if (!stateMachine.isStopped()) {
            this.tickOffset = tick;
        }

        stateMachine.setShouldResetTick(false);

        return 0;
    }

    public AzAnimationQueue getAnimationQueue() {
        return animationQueue;
    }

    public AzBoneAnimationQueueCache getBoneAnimationQueueCache() {
        return boneAnimationQueueCache;
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

    public AzKeyFrameProcessor<T> getKeyFrameProcessor() {
        return keyFrameProcessor;
    }

    public AzAnimationControllerStateMachine<T> getStateMachine() {
        return stateMachine;
    }

    public double getTransitionLength() {
        return transitionLength;
    }

    public void setShouldResetTick(boolean shouldResetTick) {
        stateMachine.setShouldResetTick(shouldResetTick);
    }

    public void setCurrentAnimation(AzQueuedAnimation currentAnimation) {
        this.currentAnimation = currentAnimation;
    }
}
