package mod.azure.azurelib.core2.animation.controller;

/**
 * A timer utility that integrates directly with an {@link AzAnimationController} to track and adjust tick values for
 * animation playback control, based on the controller's state and animation speed modifiers.
 *
 * @param <T> The type of the animatable entity being controlled by the animation controller.
 */
public class AzAnimationControllerTimer<T> {

    private final AzAnimationController<T> animationController;

    private double adjustedTick;

    private double tickOffset;

    public AzAnimationControllerTimer(AzAnimationController<T> animationController) {
        this.animationController = animationController;
    }

    /**
     * Adjust a tick value depending on the controller's current state and speed modifier.<br>
     * Is used when starting a new animation, transitioning, and a few other key areas
     */
    public void update() {
        var modifier = animationController.getAnimationSpeedModifier();
        var stateMachine = animationController.getStateMachine();
        var animContext = stateMachine.getContext().getAnimationContext();
        var animatable = animContext.animatable();
        var tick = animContext.timer().getAnimTime();

        var animationSpeed = modifier.applyAsDouble(animatable);
        adjustedTick = animationSpeed * Math.max(tick - tickOffset, 0);
    }

    public void reset() {
        var stateMachine = animationController.getStateMachine();
        var animContext = stateMachine.getContext().getAnimationContext();
        var tick = animContext.timer().getAnimTime();

        if (!stateMachine.isStopped()) {
            this.tickOffset = tick;
        }

        this.adjustedTick = 0;
    }

    public double getAdjustedTick() {
        return adjustedTick;
    }

    public void addToAdjustedTick(double adjustedTick) {
        this.adjustedTick += adjustedTick;
    }
}
