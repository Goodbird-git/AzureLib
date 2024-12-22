package mod.azure.azurelib.core2.animation.dispatch.command;

import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootCancelAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootCancelAllAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootPlayAnimationAction;

import java.util.function.UnaryOperator;

public class AzDispatchRootCommandBuilder extends AzDispatchCommandBuilder<AzDispatchRootCommandBuilder> {

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
}
