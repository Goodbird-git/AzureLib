package mod.azure.azurelib.core2.animation.controller.state.machine;

import mod.azure.azurelib.core2.animation.AzAnimationContext;
import mod.azure.azurelib.core2.animation.controller.AzAnimationController;
import mod.azure.azurelib.core2.animation.controller.state.AzAnimationState;
import mod.azure.azurelib.core2.animation.controller.state.impl.AzAnimationPauseState;
import mod.azure.azurelib.core2.animation.controller.state.impl.AzAnimationPlayState;
import mod.azure.azurelib.core2.animation.controller.state.impl.AzAnimationStopState;
import mod.azure.azurelib.core2.animation.controller.state.impl.AzAnimationTransitionState;
import mod.azure.azurelib.core2.util.StateMachine;
import mod.azure.azurelib.core2.util.StateMachineContext;

public class AzAnimationControllerStateMachine<T> extends StateMachine<AzAnimationControllerStateMachine.Context<T>, AzAnimationState<T>> {

    private final StateHolder<T> stateHolder;

    private boolean isJustStarting;

    private boolean shouldResetTick = false;

    public AzAnimationControllerStateMachine(
        StateHolder<T> stateHolder,
        AzAnimationController<T> animationController,
        AzAnimationContext<T> animationContext
    ) {
        super(stateHolder.stopState());
        this.stateHolder = stateHolder;
        getContext().stateMachine = this;
        getContext().animationController = animationController;
        getContext().animationContext = animationContext;
    }

    @Override
    public Context<T> createContext() {
        return new Context<>();
    }

    public void update() {
        super.update(getContext());

        var animContext = getContext().animationContext;
        var timer = animContext.timer();

        setJustStarting(timer.isFirstTick());
    }

    public void pause() {
        setState(stateHolder.pauseState);
    }

    public void play() {
        setState(stateHolder.playState);
    }

    public void transition() {
        setState(stateHolder.transitionState);
    }

    public void stop() {
        this.setState(stateHolder.stopState);
    }

    public boolean isPlaying() {
        return getState() == stateHolder.playState;
    }

    public boolean isPaused() {
        return getState() == stateHolder.pauseState;
    }

    public boolean isStopped() {
        return getState() == stateHolder.stopState;
    }

    public boolean isTransitioning() {
        return getState() == stateHolder.transitionState;
    }

    public void setShouldResetTick(boolean shouldResetTick) {
        this.shouldResetTick = shouldResetTick;
    }

    public boolean shouldResetTick() {
        return shouldResetTick;
    }

    public boolean isJustStarting() {
        return isJustStarting;
    }

    public void setJustStarting(boolean justStarting) {
        isJustStarting = justStarting;
    }

    public record StateHolder<T>(
        AzAnimationPlayState<T> playState,
        AzAnimationPauseState<T> pauseState,
        AzAnimationStopState<T> stopState,
        AzAnimationTransitionState<T> transitionState
    ) {}

    public static class Context<T> implements StateMachineContext {

        private AzAnimationContext<T> animationContext;

        private AzAnimationController<T> animationController;

        private AzAnimationControllerStateMachine<T> stateMachine;

        public double adjustedTick;

        private Context() {}

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
}
