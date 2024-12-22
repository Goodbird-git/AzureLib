package mod.azure.azurelib.animation.controller.state.machine;

import mod.azure.azurelib.animation.controller.state.impl.AzAnimationPauseState;
import mod.azure.azurelib.animation.controller.state.impl.AzAnimationPlayState;
import mod.azure.azurelib.animation.controller.state.impl.AzAnimationStopState;
import mod.azure.azurelib.animation.controller.state.impl.AzAnimationTransitionState;

public class StateHolder<T> {
    public AzAnimationPlayState<T> playState;
    public AzAnimationPauseState<T> pauseState;
    public AzAnimationStopState<T> stopState;
    public AzAnimationTransitionState<T> transitionState;

    public StateHolder(AzAnimationPlayState<T> playState, AzAnimationPauseState<T> pauseState, AzAnimationStopState<T> stopState, AzAnimationTransitionState<T> transitionState){
        this.playState = playState;
        this.pauseState = pauseState;
        this.stopState = stopState;
        this.transitionState = transitionState;
    }
    public AzAnimationPlayState<T> playState() {
        return playState;
    }

    public AzAnimationPauseState<T> pauseState() {
        return pauseState;
    }

    public AzAnimationStopState<T> stopState() {
        return stopState;
    }

    public AzAnimationTransitionState<T> transitionState() {
        return transitionState;
    }
}