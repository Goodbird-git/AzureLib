package mod.azure.azurelib.core2.animation.controller.state.impl;

import mod.azure.azurelib.core2.animation.controller.state.AzAnimationState;
import mod.azure.azurelib.core2.animation.controller.state.machine.AzAnimationControllerStateMachine;

public final class AzAnimationStopState<T> extends AzAnimationState<T> {

    public AzAnimationStopState() {}

    @Override
    public void onUpdate(AzAnimationControllerStateMachine.Context<T> context) {
        // Stop state does not need to do anything.
    }
}
