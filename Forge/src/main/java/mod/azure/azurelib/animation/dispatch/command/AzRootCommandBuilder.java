package mod.azure.azurelib.animation.dispatch.command;

import mod.azure.azurelib.animation.dispatch.command.action.impl.root.*;
import mod.azure.azurelib.animation.dispatch.command.sequence.AzAnimationSequence;
import mod.azure.azurelib.animation.dispatch.command.sequence.AzAnimationSequenceBuilder;
import mod.azure.azurelib.animation.easing.AzEasingType;

import java.util.function.UnaryOperator;

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
        AzAnimationSequence sequence = builderUnaryOperator.apply(new AzAnimationSequenceBuilder()).build();
        actions.add(new AzRootPlayAnimationSequenceAction(controllerName, sequence));
        return this;
    }
}
