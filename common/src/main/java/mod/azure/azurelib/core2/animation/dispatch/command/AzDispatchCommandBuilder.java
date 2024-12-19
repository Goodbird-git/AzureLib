package mod.azure.azurelib.core2.animation.dispatch.command;

import java.util.ArrayList;
import java.util.List;

import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;

public class AzDispatchCommandBuilder<T extends AzDispatchCommandBuilder<T>> {

    protected final List<AzDispatchAction> actions;

    AzDispatchCommandBuilder() {
        this.actions = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public T setSpeed(float speed) {
        // TODO:
        return self();
    }

    public T setTransitionInSpeed(float transitionSpeed) {
        // TODO:
        return self();
    }

    public T setTransitionOutSpeed(float transitionSpeed) {
        // TODO:
        return self();
    }

    public AzDispatchCommand build() {
        return new AzDispatchCommand(actions);
    }
}
