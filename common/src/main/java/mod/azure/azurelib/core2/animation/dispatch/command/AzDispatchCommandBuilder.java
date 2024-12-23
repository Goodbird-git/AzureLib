package mod.azure.azurelib.core2.animation.dispatch.command;

import java.util.ArrayList;
import java.util.List;

import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;

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
