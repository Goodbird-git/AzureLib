package mod.azure.azurelib.core2.animation.controller.state;

import mod.azure.azurelib.core2.animation.controller.state.machine.AzAnimationControllerStateMachine;
import mod.azure.azurelib.core2.util.state.State;

public abstract class AzAnimationState<T> implements State<AzAnimationControllerStateMachine.Context<T>> {

    private boolean isActive;

    protected AzAnimationState() {
        this.isActive = false;
    }

    @Override
    public void onEnter(AzAnimationControllerStateMachine.Context<T> context) {
        this.isActive = true;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void onExit(AzAnimationControllerStateMachine.Context<T> context) {
        this.isActive = false;
    }
}
