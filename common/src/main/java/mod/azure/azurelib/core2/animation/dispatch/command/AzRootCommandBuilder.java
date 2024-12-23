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

public class AzRootCommandBuilder extends AzCommandBuilder {

    public AzRootCommandBuilder append(AzCommand command) {
        actions.addAll(command.actions());
        return this;
    }

    public AzRootCommandBuilder cancelAll() {
        actions.add(new AzRootCancelAllAction());
        return this;
    }

    public AzRootCommandBuilder setEasingType(AzEasingType easingType) {
        actions.add(new AzRootSetEasingTypeAction(easingType));
        return this;
    }

    public AzRootCommandBuilder setSpeed(float speed) {
        actions.add(new AzRootSetAnimationSpeedAction(speed));
        return this;
    }

    public AzRootCommandBuilder setTransitionSpeed(float transitionSpeed) {
        actions.add(new AzRootSetTransitionSpeedAction(transitionSpeed));
        return this;
    }

    public AzRootCommandBuilder cancel(String controllerName) {
        actions.add(new AzRootCancelAction(controllerName));
        return this;
    }

    public AzRootCommandBuilder play(String controllerName, String animationName) {
        return playSequence(controllerName, builder -> builder.queue(animationName));
    }

    public AzRootCommandBuilder playSequence(
        String controllerName,
        UnaryOperator<AzAnimationSequenceBuilder> builderUnaryOperator
    ) {
        var sequence = builderUnaryOperator.apply(new AzAnimationSequenceBuilder()).build();
        actions.add(new AzRootPlayAnimationSequenceAction(controllerName, sequence));
        return this;
    }
}
