package mod.azure.azurelib.core2.animation.controller;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import mod.azure.azurelib.core.animation.EasingType;
import mod.azure.azurelib.core2.animation.AzAnimationContext;
import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzKeyFrameCallbacks;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzKeyFrameManager;
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
public class AzAnimationController<T> extends AzAbstractAnimationController {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AzAnimationController.class);

    public static <T> AzAnimationControllerBuilder<T> builder(AzAnimator<T> animator, String name) {
        return new AzAnimationControllerBuilder<>(animator, name);
    }

    private final ToDoubleFunction<T> animationSpeedModifier;

    private final AzAnimationQueue animationQueue;

    private final AzAnimationControllerStateMachine<T> stateMachine;

    private final AzAnimator<T> animator;

    private final AzBoneAnimationQueueCache<T> boneAnimationQueueCache;

    private final AzBoneSnapshotCache boneSnapshotCache;

    private final AzKeyFrameManager<T> keyFrameManager;

    private final Function<T, EasingType> overrideEasingTypeFunction;

    private final double transitionLength;

    protected AzQueuedAnimation currentAnimation;

    protected boolean needsAnimationReload = false;

    protected double tickOffset;

    /**
     * Instantiates a new {@code AnimationController}.<br>
     *
     * @param name               The name of the controller - should represent what animations it handles
     * @param transitionTickTime The amount of time (in <b>ticks</b>) that the controller should take to transition
     *                           between animations. Lerping is automatically applied where possible
     */
    AzAnimationController(
        String name,
        AzAnimator<T> animator,
        int transitionTickTime,
        ToDoubleFunction<T> animationSpeedModifier,
        AzKeyFrameCallbacks<T> keyFrameCallbacks,
        Function<T, EasingType> overrideEasingTypeFunction,
        Map<String, AzRawAnimation> triggerableAnimations
    ) {
        super(name, triggerableAnimations);

        this.animator = animator;
        this.transitionLength = transitionTickTime;
        this.animationSpeedModifier = animationSpeedModifier;
        this.overrideEasingTypeFunction = overrideEasingTypeFunction;

        this.animationQueue = new AzAnimationQueue();
        this.boneAnimationQueueCache = new AzBoneAnimationQueueCache<>(animator.context().boneCache());
        this.boneSnapshotCache = new AzBoneSnapshotCache();
        this.keyFrameManager = new AzKeyFrameManager<>(
            this,
            boneAnimationQueueCache,
            boneSnapshotCache,
            keyFrameCallbacks
        );

        var stateHolder = new AzAnimationControllerStateMachine.StateHolder<T>(
            new AzAnimationPlayState<>(),
            new AzAnimationPauseState<>(),
            new AzAnimationStopState<>(),
            new AzAnimationTransitionState<>()
        );

        this.stateMachine = new AzAnimationControllerStateMachine<>(stateHolder, this, animator.context());
    }

    @Override
    public boolean hasAnimationFinished() {
        return super.hasAnimationFinished() && stateMachine.isStopped();
    }

    /**
     * Computes the current animation speed modifier.<br>
     * This modifier defines the relative speed in which animations will be played based on the current state of the
     * game.
     *
     * @return The computed current animation speed modifier
     */
    public double computeAnimationSpeed(T animatable) {
        return animationSpeedModifier.applyAsDouble(animatable);
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

    @Override
    public boolean tryTriggerAnimation(String animName) {
        var triggeredSuccessfully = super.tryTriggerAnimation(animName);

        if (triggeredSuccessfully && stateMachine.isStopped()) {
            stateMachine.transition();
        }

        return triggeredSuccessfully;
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
        if (animator.reloadAnimations) {
            forceAnimationReset();
        }

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

        boneAnimationQueueCache.update(context, overrideEasingTypeFunction);
    }

    /**
     * Marks the controller as needing to reset its animation and state the next time
     * {@link AzAnimationController#setAnimation(T, AzRawAnimation)} is called.<br>
     * <br>
     * Use this if you have a {@link AzRawAnimation} with multiple stages and you want it to start again from the first
     * stage, or if you want to reset the currently playing animation to the start
     */
    private void forceAnimationReset() {
        this.needsAnimationReload = true;
        boneAnimationQueueCache.clear();
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

    public AzBoneAnimationQueueCache<T> getBoneAnimationQueueCache() {
        return boneAnimationQueueCache;
    }

    public AzBoneSnapshotCache getBoneSnapshotCache() {
        return boneSnapshotCache;
    }

    public @Nullable AzQueuedAnimation getCurrentAnimation() {
        return currentAnimation;
    }

    public AzKeyFrameManager<T> getKeyFrameManager() {
        return keyFrameManager;
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
