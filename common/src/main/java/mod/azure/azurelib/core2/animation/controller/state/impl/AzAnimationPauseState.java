package mod.azure.azurelib.core2.animation.controller.state.impl;

import mod.azure.azurelib.core2.animation.controller.state.machine.AzAnimationControllerStateMachine;

public final class AzAnimationPauseState<T> extends AzAnimationPlayState<T> {

    public AzAnimationPauseState() {}

    @Override
    public void onEnter(AzAnimationControllerStateMachine.Context<T> context) {
        // Do nothing, because the pause state shouldn't reset on enter.
    }

    @Override
    public void onUpdate(AzAnimationControllerStateMachine.Context<T> context) {
        super.onUpdate(context);
        // Pause state does not need to do anything.
    }

    @Override
    protected void playAgain(AzAnimationControllerStateMachine.Context<T> context) {
        // Do nothing, because the pause state shouldn't reset before playing again.
    }
}
