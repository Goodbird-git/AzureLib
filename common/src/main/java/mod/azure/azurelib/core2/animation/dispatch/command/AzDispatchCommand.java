package mod.azure.azurelib.core2.animation.dispatch.command;

import java.util.Collection;
import java.util.List;

import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.codec.AzDispatchCommandCodec;

public class AzDispatchCommand {

    public static final AzDispatchCommandCodec CODEC = new AzDispatchCommandCodec();

    private final List<AzDispatchAction> actions;

    public AzDispatchCommand(List<AzDispatchAction> actions) {
        this.actions = actions;
    }

    public static AzDispatchRootCommandBuilder builder() {
        return new AzDispatchRootCommandBuilder();
    }

    public static AzDispatchCommand playAnimation(String controllerName, String animationName) {
        return builder()
            .playAnimation(controllerName, animationName)
            .build();
    }

    public Collection<? extends AzDispatchAction> getActions() {
        return actions;
    }
}
