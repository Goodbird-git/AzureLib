package mod.azure.azurelib.core2.animation.controller;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import mod.azure.azurelib.core.animation.EasingType;
import mod.azure.azurelib.core.keyframe.AnimationPoint;
import mod.azure.azurelib.core.keyframe.BoneAnimationQueue;
import mod.azure.azurelib.core.keyframe.Keyframe;
import mod.azure.azurelib.core.keyframe.KeyframeLocation;
import mod.azure.azurelib.core.keyframe.event.data.KeyFrameData;
import mod.azure.azurelib.core.math.Constant;
import mod.azure.azurelib.core.math.IValue;
import mod.azure.azurelib.core.molang.MolangParser;
import mod.azure.azurelib.core.molang.MolangQueries;
import mod.azure.azurelib.core.object.Axis;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.core.state.BoneSnapshot;
import mod.azure.azurelib.core2.animation.AzAnimationProcessor;
import mod.azure.azurelib.core2.animation.AzAnimationState;
import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.controller.handler.AzAnimationStateHandler;
import mod.azure.azurelib.core2.animation.controller.handler.AzCustomKeyframeHandler;
import mod.azure.azurelib.core2.animation.controller.handler.AzParticleKeyframeHandler;
import mod.azure.azurelib.core2.animation.controller.handler.AzSoundKeyframeHandler;
import mod.azure.azurelib.core2.animation.event.AzCustomInstructionKeyframeEvent;
import mod.azure.azurelib.core2.animation.event.AzParticleKeyframeEvent;
import mod.azure.azurelib.core2.animation.event.AzSoundKeyframeEvent;
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

    protected final AzAnimationStateHandler<T> stateHandler;

    protected final Map<String, BoneAnimationQueue> boneAnimationQueues = new Object2ObjectOpenHashMap<>();

    protected final Map<String, BoneSnapshot> boneSnapshots = new Object2ObjectOpenHashMap<>();

    protected final Map<String, AzRawAnimation> triggerableAnimations = new Object2ObjectOpenHashMap<>(0);

    protected final Set<KeyFrameData> executedKeyFrames = new ObjectOpenHashSet<>();

    private final AzAnimator<T> animator;

    protected Queue<AzQueuedAnimation> animationQueue = new LinkedList<>();

    protected boolean isJustStarting = false;

    protected boolean needsAnimationReload = false;

    protected boolean shouldResetTick = false;

    protected boolean justStartedTransition = false;

    protected AzSoundKeyframeHandler<T> soundKeyframeHandler = null;

    protected AzParticleKeyframeHandler<T> particleKeyframeHandler = null;

    protected AzCustomKeyframeHandler<T> customKeyframeHandler = null;

    protected AzRawAnimation triggeredAnimation = null;

    protected boolean handlingTriggeredAnimations = false;

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
     * @param animationHandler   The {@link AzAnimationStateHandler} animation state handler responsible for deciding
     *                           which animations to play
     */
    public AzAnimationController(
        AzAnimator<T> animator,
        String name,
        int transitionTickTime,
        AzAnimationStateHandler<T> animationHandler
    ) {
        this.animator = animator;
        this.name = name;
        this.transitionLength = transitionTickTime;
        this.stateHandler = animationHandler;
    }

    /**
     * Applies the given {@link AzSoundKeyframeHandler} to this controller, for handling {@link AzSoundKeyframeEvent
     * sound keyframe instructions}.
     *
     * @return this
     */
    public AzAnimationController<T> setSoundKeyframeHandler(AzSoundKeyframeHandler<T> soundHandler) {
        this.soundKeyframeHandler = soundHandler;

        return this;
    }

    /**
     * Applies the given {@link AzParticleKeyframeHandler} to this controller, for handling
     * {@link AzParticleKeyframeEvent particle keyframe instructions}.
     *
     * @return this
     */
    public AzAnimationController<T> setParticleKeyframeHandler(AzParticleKeyframeHandler<T> particleHandler) {
        this.particleKeyframeHandler = particleHandler;

        return this;
    }

    /**
     * Applies the given {@link AzCustomKeyframeHandler} to this controller, for handling
     * {@link AzCustomInstructionKeyframeEvent sound keyframe instructions}.
     *
     * @return this
     */
    public AzAnimationController<T> setCustomInstructionKeyframeHandler(
        AzCustomKeyframeHandler<T> customInstructionHandler
    ) {
        this.customKeyframeHandler = customInstructionHandler;

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
     * Tells the AnimationController that you want to receive the {@link AzAnimationStateHandler} while a triggered
     * animation is playing.<br>
     * <br>
     * This has no effect if no triggered animation has been registered, or one isn't currently playing.<br>
     * If a triggered animation is playing, it can be checked in your AnimationStateHandler via
     * {@link AzAnimationController#isPlayingTriggeredAnimation()}
     */
    public AzAnimationController<T> receiveTriggeredAnimations() {
        this.handlingTriggeredAnimations = true;

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
     * Used for custom handling if {@link AzAnimationController#receiveTriggeredAnimations()} was marked
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
                this.animationQueue = animations;
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

            if (
                !hasAnimationFinished() && (!handlingTriggeredAnimations || stateHandler.handle(
                    state
                ) == PlayState.CONTINUE)
            ) {
                return PlayState.CONTINUE;
            }

            this.triggeredAnimation = null;
            this.needsAnimationReload = true;
        }

        return stateHandler.handle(state);
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
            processCurrentAnimation(animatable, adjustedTick, seekTime, crashWhenCantFindBone);
        } else if (animationState == AzAnimationControllerState.TRANSITIONING) {
            if (adjustedTick == 0 || isJustStarting) {
                this.justStartedTransition = false;
                this.currentAnimation = animationQueue.poll();

                resetEventKeyFrames();

                if (currentAnimation == null) {
                    return;
                }

                saveSnapshotsForAnimation(currentAnimation, snapshots);
            }

            if (currentAnimation != null) {
                MolangParser.INSTANCE.setValue(MolangQueries.ANIM_TIME, () -> 0);

                for (var boneAnimation : currentAnimation.animation().boneAnimations()) {
                    var boneAnimationQueue = boneAnimationQueues.get(boneAnimation.boneName());
                    var boneSnapshot = boneSnapshots.get(boneAnimation.boneName());
                    var bone = bones.get(boneAnimation.boneName());

                    if (bone == null) {
                        if (crashWhenCantFindBone)
                            throw new NoSuchElementException("Could not find bone: " + boneAnimation.boneName());

                        continue;
                    }

                    var rotationKeyFrames = boneAnimation.rotationKeyFrames();
                    var positionKeyFrames = boneAnimation.positionKeyFrames();
                    var scaleKeyFrames = boneAnimation.scaleKeyFrames();

                    if (!rotationKeyFrames.xKeyframes().isEmpty()) {
                        boneAnimationQueue.addNextRotation(
                            null,
                            adjustedTick,
                            transitionLength,
                            boneSnapshot,
                            bone.getInitialSnapshot(),
                            getAnimationPointAtTick(rotationKeyFrames.xKeyframes(), 0, true, Axis.X),
                            getAnimationPointAtTick(rotationKeyFrames.yKeyframes(), 0, true, Axis.Y),
                            getAnimationPointAtTick(rotationKeyFrames.zKeyframes(), 0, true, Axis.Z)
                        );
                    }

                    if (!positionKeyFrames.xKeyframes().isEmpty()) {
                        boneAnimationQueue.addNextPosition(
                            null,
                            adjustedTick,
                            transitionLength,
                            boneSnapshot,
                            getAnimationPointAtTick(positionKeyFrames.xKeyframes(), 0, false, Axis.X),
                            getAnimationPointAtTick(positionKeyFrames.yKeyframes(), 0, false, Axis.Y),
                            getAnimationPointAtTick(positionKeyFrames.zKeyframes(), 0, false, Axis.Z)
                        );
                    }

                    if (!scaleKeyFrames.xKeyframes().isEmpty()) {
                        boneAnimationQueue.addNextScale(
                            null,
                            adjustedTick,
                            transitionLength,
                            boneSnapshot,
                            getAnimationPointAtTick(scaleKeyFrames.xKeyframes(), 0, false, Axis.X),
                            getAnimationPointAtTick(scaleKeyFrames.yKeyframes(), 0, false, Axis.Y),
                            getAnimationPointAtTick(scaleKeyFrames.zKeyframes(), 0, false, Axis.Z)
                        );
                    }
                }
            }
        }
    }

    /**
     * Handle the current animation's state modifications and translations
     *
     * @param adjustedTick          The controller-adjusted tick for animation purposes
     * @param seekTime              The lerped tick (current tick + partial tick)
     * @param crashWhenCantFindBone Whether the controller should throw an exception when unable to find the required
     *                              bone, or continue with the remaining bones
     */
    protected void processCurrentAnimation(
        T animatable,
        double adjustedTick,
        double seekTime,
        boolean crashWhenCantFindBone
    ) {
        if (adjustedTick >= currentAnimation.animation().length()) {
            if (
                currentAnimation.loopType().shouldPlayAgain(animatable, this, this.currentAnimation.animation())
            ) {
                if (animationState != AzAnimationControllerState.PAUSED) {
                    this.shouldResetTick = true;

                    adjustedTick = adjustTick(animatable, seekTime);
                    resetEventKeyFrames();
                }
            } else {
                var nextAnimation = animationQueue.peek();

                resetEventKeyFrames();

                if (nextAnimation == null) {
                    this.animationState = AzAnimationControllerState.STOPPED;

                    return;
                } else {
                    this.animationState = AzAnimationControllerState.TRANSITIONING;
                    this.shouldResetTick = true;
                    this.currentAnimation = nextAnimation;
                }
            }
        }

        final double finalAdjustedTick = adjustedTick;

        MolangParser.INSTANCE.setMemoizedValue(MolangQueries.ANIM_TIME, () -> finalAdjustedTick / 20d);

        for (var boneAnimation : currentAnimation.animation().boneAnimations()) {
            var boneAnimationQueue = boneAnimationQueues.get(boneAnimation.boneName());

            if (boneAnimationQueue == null) {
                if (crashWhenCantFindBone)
                    throw new NoSuchElementException("Could not find bone: " + boneAnimation.boneName());

                continue;
            }

            var rotationKeyFrames = boneAnimation.rotationKeyFrames();
            var positionKeyFrames = boneAnimation.positionKeyFrames();
            var scaleKeyFrames = boneAnimation.scaleKeyFrames();

            if (!rotationKeyFrames.xKeyframes().isEmpty()) {
                boneAnimationQueue.addRotations(
                    getAnimationPointAtTick(rotationKeyFrames.xKeyframes(), adjustedTick, true, Axis.X),
                    getAnimationPointAtTick(rotationKeyFrames.yKeyframes(), adjustedTick, true, Axis.Y),
                    getAnimationPointAtTick(rotationKeyFrames.zKeyframes(), adjustedTick, true, Axis.Z)
                );
            }

            if (!positionKeyFrames.xKeyframes().isEmpty()) {
                boneAnimationQueue.addPositions(
                    getAnimationPointAtTick(positionKeyFrames.xKeyframes(), adjustedTick, false, Axis.X),
                    getAnimationPointAtTick(positionKeyFrames.yKeyframes(), adjustedTick, false, Axis.Y),
                    getAnimationPointAtTick(positionKeyFrames.zKeyframes(), adjustedTick, false, Axis.Z)
                );
            }

            if (!scaleKeyFrames.xKeyframes().isEmpty()) {
                boneAnimationQueue.addScales(
                    getAnimationPointAtTick(scaleKeyFrames.xKeyframes(), adjustedTick, false, Axis.X),
                    getAnimationPointAtTick(scaleKeyFrames.yKeyframes(), adjustedTick, false, Axis.Y),
                    getAnimationPointAtTick(scaleKeyFrames.zKeyframes(), adjustedTick, false, Axis.Z)
                );
            }
        }

        adjustedTick += this.transitionLength;

        for (var keyframeData : currentAnimation.animation().keyFrames().sounds()) {
            if (adjustedTick >= keyframeData.getStartTick() && executedKeyFrames.add(keyframeData)) {
                if (soundKeyframeHandler == null) {
                    LOGGER.warn(
                        "Sound Keyframe found for {} -> {}, but no keyframe handler registered",
                        animatable.getClass().getSimpleName(),
                        getName()
                    );
                    break;
                }

                soundKeyframeHandler.handle(
                    new AzSoundKeyframeEvent<>(animatable, adjustedTick, this, keyframeData)
                );
            }
        }

        for (var keyframeData : currentAnimation.animation().keyFrames().particles()) {
            if (adjustedTick >= keyframeData.getStartTick() && executedKeyFrames.add(keyframeData)) {
                if (particleKeyframeHandler == null) {
                    LOGGER.warn(
                        "Particle Keyframe found for {} -> {}, but no keyframe handler registered",
                        animatable.getClass().getSimpleName(),
                        getName()
                    );
                    break;
                }

                particleKeyframeHandler.handle(
                    new AzParticleKeyframeEvent<>(animatable, adjustedTick, this, keyframeData)
                );
            }
        }

        for (
            var keyframeData : currentAnimation.animation()
                .keyFrames()
                .customInstructions()
        ) {
            if (adjustedTick >= keyframeData.getStartTick() && executedKeyFrames.add(keyframeData)) {
                if (customKeyframeHandler == null) {
                    LOGGER.warn(
                        "Custom Instruction Keyframe found for {} -> {}, but no keyframe handler registered",
                        animatable.getClass().getSimpleName(),
                        getName()
                    );
                    break;
                }

                customKeyframeHandler.handle(
                    new AzCustomInstructionKeyframeEvent<>(animatable, adjustedTick, this, keyframeData)
                );
            }
        }

        if (
            this.transitionLength == 0 && this.shouldResetTick
                && this.animationState == AzAnimationControllerState.TRANSITIONING
        ) {
            this.currentAnimation = this.animationQueue.poll();
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
     * Cache the relevant {@link BoneSnapshot BoneSnapshots} for the current {@link AzQueuedAnimation} for animation
     * lerping
     *
     * @param animation The {@code QueuedAnimation} to filter {@code BoneSnapshots} for
     * @param snapshots The master snapshot collection to pull filter from
     */
    protected void saveSnapshotsForAnimation(
        AzQueuedAnimation animation,
        Map<String, BoneSnapshot> snapshots
    ) {
        if (animation.animation().boneAnimations() == null) {
            return;
        }

        for (var snapshot : snapshots.values()) {
            for (var boneAnimation : animation.animation().boneAnimations()) {
                if (boneAnimation.boneName().equals(snapshot.getBone().getName())) {
                    boneSnapshots.put(boneAnimation.boneName(), BoneSnapshot.copy(snapshot));
                    break;
                }
            }
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
    protected double adjustTick(T animatable, double tick) {
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
     * Convert a {@link KeyframeLocation} to an {@link AnimationPoint}
     */
    protected AnimationPoint getAnimationPointAtTick(
        List<Keyframe<IValue>> frames,
        double tick,
        boolean isRotation,
        Axis axis
    ) {
        var location = getCurrentKeyFrameLocation(frames, tick);
        var currentFrame = location.keyframe();
        var startValue = currentFrame.startValue().get();
        var endValue = currentFrame.endValue().get();

        if (isRotation) {
            if (!(currentFrame.startValue() instanceof Constant)) {
                startValue = Math.toRadians(startValue);

                if (axis == Axis.X || axis == Axis.Y) {
                    startValue *= -1;
                }
            }

            if (!(currentFrame.endValue() instanceof Constant)) {
                endValue = Math.toRadians(endValue);

                if (axis == Axis.X || axis == Axis.Y) {
                    endValue *= -1;
                }
            }
        }

        return new AnimationPoint(currentFrame, location.startTick(), currentFrame.length(), startValue, endValue);
    }

    /**
     * Returns the {@link Keyframe} relevant to the current tick time
     *
     * @param frames     The list of {@code KeyFrames} to filter through
     * @param ageInTicks The current tick time
     * @return A new {@code KeyFrameLocation} containing the current {@code KeyFrame} and the tick time used to find it
     */
    protected KeyframeLocation<Keyframe<IValue>> getCurrentKeyFrameLocation(
        List<Keyframe<IValue>> frames,
        double ageInTicks
    ) {
        var totalFrameTime = 0;

        for (var frame : frames) {
            totalFrameTime += frame.length();

            if (totalFrameTime > ageInTicks) {
                return new KeyframeLocation<>(frame, (ageInTicks - (totalFrameTime - frame.length())));
            }
        }

        return new KeyframeLocation<>(frames.get(frames.size() - 1), ageInTicks);
    }

    /**
     * Clear the {@link KeyFrameData} cache in preparation for the next animation
     */
    protected void resetEventKeyFrames() {
        executedKeyFrames.clear();
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
}
