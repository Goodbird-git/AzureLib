package mod.azure.azurelib.core2.animation.dispatch.command;

import java.util.ArrayList;
import java.util.List;

import mod.azure.azurelib.core2.animation.dispatch.command.action.AzAction;

public abstract class AzCommandBuilder {

    protected final List<AzAction> actions;

    protected AzCommandBuilder() {
        this.actions = new ArrayList<>();
    }

    public AzCommand build() {
        return new AzCommand(actions);
    }
}
