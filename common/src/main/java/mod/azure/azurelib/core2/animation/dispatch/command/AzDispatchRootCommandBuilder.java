package mod.azure.azurelib.core2.animation.dispatch.command;

import java.util.function.UnaryOperator;

import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootCancelAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootCancelAllAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootPlayAnimationSequenceAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootSetAnimationSpeedAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootSetEasingTypeAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root.AzRootSetTransitionSpeedAction;
import mod.azure.azurelib.core2.animation.dispatch.command.sequence.AzAnimationSequenceBuilder;
import mod.azure.azurelib.core2.animation.easing.AzEasingType;

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

    public AzDispatchRootCommandBuilder setTransitionSpeed(float transitionSpeed) {
        actions.add(new AzRootSetTransitionSpeedAction(transitionSpeed));
        return self();
    }

    public AzDispatchRootCommandBuilder cancel(String controllerName) {
        actions.add(new AzRootCancelAction(controllerName));
        return self();
    }

    public AzDispatchRootCommandBuilder play(String controllerName, String animationName) {
        var builder = new AzAnimationSequenceBuilder()
            .queue(animationName);
        var sequence = builder.build();
        actions.add(new AzRootPlayAnimationSequenceAction(controllerName, sequence));
        return self();
    }

    public AzDispatchRootCommandBuilder playSequence(String controllerName, UnaryOperator<AzAnimationSequenceBuilder> builderUnaryOperator) {
        var builder = new AzAnimationSequenceBuilder();
        builderUnaryOperator.apply(builder);
        var sequence = builder.build();
        actions.add(new AzRootPlayAnimationSequenceAction(controllerName, sequence));
        return self();
    }
}
