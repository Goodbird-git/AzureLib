package mod.azure.azurelib.core2.animation.dispatch.command;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootCancelAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootCancelAllAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootPlayAnimationAction;

public class AzDispatchRootCommandBuilder extends AzDispatchCommandBuilder<AzDispatchRootCommandBuilder> {

    private final List<AzDispatchAction> actions;

    AzDispatchRootCommandBuilder() {
        this.actions = new ArrayList<>();
    }

    public AzDispatchRootCommandBuilder cancelAll() {
        actions.add(new AzRootCancelAllAction());
        return self();
    }

    public AzDispatchRootCommandBuilder cancel(String controllerName) {
        actions.add(new AzRootCancelAction(controllerName));
        return self();
    }

    public AzDispatchRootCommandBuilder forController(
        String controllerName,
        UnaryOperator<AzDispatchControllerCommandBuilder> builderUnaryOperator
    ) {
        var builder = new AzDispatchControllerCommandBuilder();
        builderUnaryOperator.apply(builder);
        var command = builder.build();
        actions.addAll(command.getActions());
        return self();
    }

    public AzDispatchRootCommandBuilder playAnimation(String controllerName, String animationName) {
        actions.add(new AzRootPlayAnimationAction(controllerName, animationName));
        return self();
    }

    public AzDispatchCommand build() {
        return new AzDispatchCommand(actions);
    }
}
