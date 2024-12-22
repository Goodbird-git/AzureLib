package mod.azure.azurelib.animation.controller.state.machine;

import mod.azure.azurelib.animation.AzAnimationContext;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.util.state.StateMachineContext;

public class Context<T> implements StateMachineContext {

    AzAnimationContext<T> animationContext;

    AzAnimationController<T> animationController;

    AzAnimationControllerStateMachine<T> stateMachine;

    Context() {}

    public AzAnimationContext<T> animationContext() {
        return animationContext;
    }

    public AzAnimationController<T> animationController() {
        return animationController;
    }

    public AzAnimationControllerStateMachine<T> stateMachine() {
        return stateMachine;
    }
}
