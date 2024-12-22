package mod.azure.azurelib.core2.animation.controller;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import mod.azure.azurelib.core2.animation.AzAnimationContext;
import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzKeyFrameCallbacks;
import mod.azure.azurelib.core2.animation.controller.keyframe.AzKeyFrameManager;
import mod.azure.azurelib.core2.animation.controller.state.impl.AzAnimationPauseState;
import mod.azure.azurelib.core2.animation.controller.state.impl.AzAnimationPlayState;
import mod.azure.azurelib.core2.animation.controller.state.impl.AzAnimationStopState;
import mod.azure.azurelib.core2.animation.controller.state.impl.AzAnimationTransitionState;
import mod.azure.azurelib.core2.animation.controller.state.machine.AzAnimationControllerStateMachine;
import mod.azure.azurelib.core2.animation.primitive.AzBakedAnimation;
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

    private final AzAnimationControllerTimer<T> controllerTimer;

    private final AzAnimationProperties animationProperties;

    private final AzAnimationQueue animationQueue;

    private final AzAnimationControllerStateMachine<T> stateMachine;

    private final AzAnimator<T> animator;

    private final AzBoneAnimationQueueCache<T> boneAnimationQueueCache;

    private final AzBoneSnapshotCache boneSnapshotCache;

    private final AzKeyFrameManager<T> keyFrameManager;

    protected AzQueuedAnimation currentAnimation;

    AzAnimationController(
        String name,
        AzAnimator<T> animator,
        AzAnimationProperties animationProperties,
        AzKeyFrameCallbacks<T> keyFrameCallbacks,
        Map<String, AzRawAnimation> triggerableAnimations
    ) {
        super(name, triggerableAnimations);

        this.animator = animator;
        this.controllerTimer = new AzAnimationControllerTimer<>(this);
        this.animationProperties = animationProperties;

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
                ? AzBakedAnimation.generateWaitAnimation(stage.additionalTicks())
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
     *
     * @deprecated
     */
    @Deprecated(forRemoval = true)
    public void setAnimation(T animatable, AzRawAnimation rawAnimation) {
        if (rawAnimation == null || rawAnimation.getAnimationStages().isEmpty()) {
            stateMachine.stop();
            return;
        }

        if (!rawAnimation.equals(currentRawAnimation)) {
            var animations = tryCreateAnimationQueue(animatable, rawAnimation);

            if (!animations.isEmpty()) {
                animationQueue.clear();
                animationQueue.addAll(animations);
                this.currentRawAnimation = rawAnimation;
                stateMachine.transition();
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
        }
    }

    /**
     * This method is called every frame in order to populate the animation point queues, and process animation state
     * logic.
     */
    public void update(AzAnimationContext<T> context) {
        var animatable = context.animatable();

        handleAnimationState(animatable);

        // Adjust the tick before making any updates.
        controllerTimer.update();
        // Run state machine updates.
        stateMachine.update();

        boneAnimationQueueCache.update(animationProperties.easingType());
    }

    public AzAnimationProperties animationProperties() {
        return animationProperties;
    }

    public AzAnimationQueue animationQueue() {
        return animationQueue;
    }

    public AzBoneAnimationQueueCache<T> boneAnimationQueueCache() {
        return boneAnimationQueueCache;
    }

    public AzBoneSnapshotCache boneSnapshotCache() {
        return boneSnapshotCache;
    }

    public AzAnimationControllerTimer<T> controllerTimer() {
        return controllerTimer;
    }

    public @Nullable AzQueuedAnimation currentAnimation() {
        return currentAnimation;
    }

    public AzKeyFrameManager<T> keyFrameManager() {
        return keyFrameManager;
    }

    public AzAnimationControllerStateMachine<T> stateMachine() {
        return stateMachine;
    }

    public void setCurrentAnimation(AzQueuedAnimation currentAnimation) {
        this.currentAnimation = currentAnimation;
    }
}
