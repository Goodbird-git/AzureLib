package mod.azure.azurelib.animation.dispatch.command;

import mod.azure.azurelib.animation.dispatch.command.action.impl.root.*;
import mod.azure.azurelib.animation.easing.AzEasingType;

import java.util.function.UnaryOperator;

public class AzDispatchRootCommandBuilder extends AzDispatchCommandBuilder<AzDispatchRootCommandBuilder> {

    public AzDispatchRootCommandBuilder cancelAll() {
        actions.add(new AzRootCancelAllAction());
        return self();
    }

    public AzDispatchRootCommandBuilder setEasingType(AzEasingType easingType) {
        actions.add(new AzRootSetEasingTypeAction(easingType));
        return self();
    }

    public AzDispatchRootCommandBuilder setSpeed(float speed) {
        actions.add(new AzRootSetAnimationSpeedAction(speed));
        return self();
    }

    public AzDispatchRootCommandBuilder setTransitionInSpeed(float transitionSpeed) {
        actions.add(new AzRootSetTransitionInSpeedAction(transitionSpeed));
        return self();
    }

    // TODO:
    // public AzDispatchRootCommandBuilder setTransitionOutSpeed(float transitionSpeed) {
    // // TODO:
    // return self();
    // }

    public AzDispatchRootCommandBuilder cancel(String controllerName) {
        actions.add(new AzRootCancelAction(controllerName));
        return self();
    }

    public AzDispatchRootCommandBuilder forController(
        String controllerName,
        UnaryOperator<AzDispatchControllerCommandBuilder> builderUnaryOperator
    ) {
        AzDispatchControllerCommandBuilder builder = new AzDispatchControllerCommandBuilder();
        builderUnaryOperator.apply(builder);
        AzDispatchCommand command = builder.build();
        actions.addAll(command.getActions());
        return self();
    }

    public AzDispatchRootCommandBuilder playAnimation(String controllerName, String animationName) {
        actions.add(new AzRootPlayAnimationAction(controllerName, animationName));
        return self();
    }
}
