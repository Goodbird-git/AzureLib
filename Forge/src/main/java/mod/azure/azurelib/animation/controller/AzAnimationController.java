package mod.azure.azurelib.animation.controller;

import mod.azure.azurelib.animation.AzAnimationContext;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.controller.keyframe.AzKeyframeCallbacks;
import mod.azure.azurelib.animation.controller.keyframe.AzKeyframeManager;
import mod.azure.azurelib.animation.controller.state.impl.AzAnimationPauseState;
import mod.azure.azurelib.animation.controller.state.impl.AzAnimationPlayState;
import mod.azure.azurelib.animation.controller.state.impl.AzAnimationStopState;
import mod.azure.azurelib.animation.controller.state.impl.AzAnimationTransitionState;
import mod.azure.azurelib.animation.controller.state.machine.AzAnimationControllerStateMachine;
import mod.azure.azurelib.animation.controller.state.machine.StateHolder;
import mod.azure.azurelib.animation.primitive.AzBakedAnimation;
import mod.azure.azurelib.animation.primitive.AzQueuedAnimation;
import mod.azure.azurelib.animation.primitive.AzRawAnimation;
import mod.azure.azurelib.animation.primitive.AzStage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The actual controller that handles the playing and usage of animations, including their various keyframes and
 * instruction markers. Each controller can only play a single animation at a time - for example you may have one
 * controller to animate walking, one to control attacks, one to control size, etc.
 */
public class AzAnimationController<T> extends AzAbstractAnimationController {

    protected static final Logger LOGGER = LogManager.getLogger(AzAnimationController.class);

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

    private final AzKeyframeManager<T> keyframeManager;

    protected AzQueuedAnimation currentAnimation;

    /**
     * Instantiates a new {@code AnimationController}.<br>
     */
    AzAnimationController(
        String name,
        AzAnimator<T> animator,
        AzAnimationProperties animationProperties,
        AzKeyframeCallbacks<T> keyframeCallbacks,
        Map<String, AzRawAnimation> triggerableAnimations
    ) {
        super(name, triggerableAnimations);

        this.animator = animator;
        this.controllerTimer = new AzAnimationControllerTimer<>(this);
        this.animationProperties = animationProperties;

        this.animationQueue = new AzAnimationQueue();
        this.boneAnimationQueueCache = new AzBoneAnimationQueueCache<>(animator.context().boneCache());
        this.boneSnapshotCache = new AzBoneSnapshotCache();
        this.keyframeManager = new AzKeyframeManager<>(
            this,
            boneAnimationQueueCache,
            boneSnapshotCache,
                keyframeCallbacks
        );

        StateHolder<T> stateHolder = new StateHolder<>(
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
        List<AzStage> stages = rawAnimation.getAnimationStages();
        ArrayList<AzQueuedAnimation> animations = new ArrayList<>();

        for (AzStage stage : stages) {
            AzBakedAnimation animation = Objects.equals(stage.animationName(), AzStage.WAIT)
                ? AzBakedAnimation.generateWaitAnimation(stage.additionalTicks())
                : animator.getAnimation(animatable, stage.animationName());

            if (animation == null) {
                LOGGER.warn(
                    "Unable to find animation: {} for {}",
                    stage.animationName(),
                    animatable.getClass().getSimpleName()
                );
                return new ArrayList<>();
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
    @Deprecated()
    public void setAnimation(T animatable, AzRawAnimation rawAnimation) {
        if (rawAnimation == null || rawAnimation.getAnimationStages().isEmpty()) {
            stateMachine.stop();
            return;
        }

        if (!rawAnimation.equals(currentRawAnimation)) {
            List<AzQueuedAnimation> animations = tryCreateAnimationQueue(animatable, rawAnimation);

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
        boolean triggeredSuccessfully = super.tryTriggerAnimation(animName);

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
        T animatable = context.animatable();

        handleAnimationState(animatable);

        // Adjust the tick before making any updates.
        controllerTimer.update();
        // Run state machine updates.
        stateMachine.update();

        boneAnimationQueueCache.update(animationProperties.easingType());
    }

    /**
     * Marks the controller as needing to reset its animation and state the next time
     * {@link AzAnimationController#setAnimation(T, AzRawAnimation)} is called.<br>
     * <br>
     * Use this if you have a {@link AzRawAnimation} with multiple stages, and you want it to start again from the first
     * stage, or if you want to reset the currently playing animation to the start
     */
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

    public AzQueuedAnimation currentAnimation() {
        return currentAnimation;
    }

    public AzKeyframeManager<T> keyframeManager() {
        return keyframeManager;
    }

    public AzAnimationControllerStateMachine<T> stateMachine() {
        return stateMachine;
    }

    public void setCurrentAnimation(AzQueuedAnimation currentAnimation) {
        this.currentAnimation = currentAnimation;
    }
}
