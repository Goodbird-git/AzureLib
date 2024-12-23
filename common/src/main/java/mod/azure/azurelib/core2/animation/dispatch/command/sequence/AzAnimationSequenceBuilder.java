package mod.azure.azurelib.core2.animation.dispatch.command.sequence;

import mod.azure.azurelib.core2.animation.dispatch.command.stage.AzAnimationStage;
import mod.azure.azurelib.core2.animation.property.AzAnimationStageProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class AzAnimationSequenceBuilder {

    private final List<AzAnimationStage> stages;

    public AzAnimationSequenceBuilder() {
        this.stages = new ArrayList<>();
    }

    public AzAnimationSequenceBuilder queue(String animationName) {
        stages.add(new AzAnimationStage(animationName, AzAnimationStageProperties.EMPTY));
        return this;
    }

    public AzAnimationSequenceBuilder queue(String animationName, UnaryOperator<AzAnimationStageProperties> builderUnaryOperator) {
        var properties = builderUnaryOperator.apply(AzAnimationStageProperties.EMPTY);
        stages.add(new AzAnimationStage(animationName, properties));
        return this;
    }

    public AzAnimationSequence build() {
        return new AzAnimationSequence(stages);
    }
}
