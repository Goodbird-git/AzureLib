package mod.azure.azurelib.animation.dispatch.command;

import mod.azure.azurelib.animation.dispatch.command.action.AzDispatchAction;

import java.util.ArrayList;
import java.util.List;

public abstract class AzDispatchCommandBuilder<T extends AzDispatchCommandBuilder<T>> {

    protected final List<AzDispatchAction> actions;

    protected AzDispatchCommandBuilder() {
        this.actions = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public AzDispatchCommand build() {
        return new AzDispatchCommand(actions);
    }
}
