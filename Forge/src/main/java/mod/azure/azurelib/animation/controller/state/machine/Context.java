package mod.azure.azurelib.animation.controller.state.machine;

import mod.azure.azurelib.animation.AzAnimationContext;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.util.state.StateMachineContext;

public class Context<T> implements StateMachineContext {

    AzAnimationContext<T> animationContext;

    AzAnimationController<T> animationController;

    AzAnimationControllerStateMachine<T> stateMachine;

    Context() {}

    public AzAnimationContext<T> getAnimationContext() {
        return animationContext;
    }

    public AzAnimationController<T> getAnimationController() {
        return animationController;
    }

    public AzAnimationControllerStateMachine<T> getStateMachine() {
        return stateMachine;
    }
}
