package mod.azure.azurelib.core2.animation.controller.state.impl;

import mod.azure.azurelib.core2.animation.controller.state.AzAnimationState;
import mod.azure.azurelib.core2.animation.controller.state.machine.AzAnimationControllerStateMachine;

public final class AzAnimationPauseState<T> extends AzAnimationState<T> {

    public AzAnimationPauseState() {}

    @Override
    public void onUpdate(AzAnimationControllerStateMachine.Context<T> context) {
        // Pause state does not need to do anything.
    }
}
