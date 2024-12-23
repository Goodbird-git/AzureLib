package mod.azure.azurelib.animation.dispatch.command;

import mod.azure.azurelib.animation.dispatch.command.action.impl.root.*;
import mod.azure.azurelib.animation.dispatch.command.sequence.AzAnimationSequence;
import mod.azure.azurelib.animation.dispatch.command.sequence.AzAnimationSequenceBuilder;
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

    public AzDispatchRootCommandBuilder setTransitionSpeed(float transitionSpeed) {
        actions.add(new AzRootSetTransitionSpeedAction(transitionSpeed));
        return self();
    }

    public AzDispatchRootCommandBuilder cancel(String controllerName) {
        actions.add(new AzRootCancelAction(controllerName));
        return self();
    }

    public AzDispatchRootCommandBuilder play(String controllerName, String animationName) {
        return playSequence(controllerName, builder -> builder.queue(animationName));
    }

    public AzDispatchRootCommandBuilder playSequence(
            String controllerName,
            UnaryOperator<AzAnimationSequenceBuilder> builderUnaryOperator
    ) {
        AzAnimationSequence sequence = builderUnaryOperator.apply(new AzAnimationSequenceBuilder()).build();
        actions.add(new AzRootPlayAnimationSequenceAction(controllerName, sequence));
        return self();
    }
}
