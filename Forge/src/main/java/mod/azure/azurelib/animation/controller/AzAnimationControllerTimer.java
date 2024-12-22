package mod.azure.azurelib.animation.controller;

import mod.azure.azurelib.animation.AzAnimationContext;
import mod.azure.azurelib.animation.controller.state.machine.AzAnimationControllerStateMachine;

import java.util.function.ToDoubleFunction;

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
        ToDoubleFunction<T> modifier = animationController.getAnimationSpeedModifier();
        AzAnimationControllerStateMachine<T> stateMachine = animationController.getStateMachine();
        AzAnimationContext<T> animContext = stateMachine.getContext().getAnimationContext();
        T animatable = animContext.animatable();
        double tick = animContext.timer().getAnimTime();

        double animationSpeed = modifier.applyAsDouble(animatable);
        adjustedTick = animationSpeed * Math.max(tick - tickOffset, 0);
    }

    public void reset() {
        AzAnimationControllerStateMachine<T> stateMachine = animationController.getStateMachine();
        AzAnimationContext<T> animContext = stateMachine.getContext().getAnimationContext();
        double tick = animContext.timer().getAnimTime();

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
