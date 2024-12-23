package mod.azure.azurelib.animation.dispatch.command.sequence;

import mod.azure.azurelib.animation.dispatch.command.stage.AzAnimationStage;
import mod.azure.azurelib.animation.property.codec.AzListStreamCodec;

import java.util.List;

public class AzAnimationSequence {

    public List<AzAnimationStage> stages;

    public static final StreamCodec<FriendlyByteBuf, AzAnimationSequence> CODEC = StreamCodec.composite(
            new AzListStreamCodec<>(AzAnimationStage.CODEC),
            AzAnimationSequence::stages,
            AzAnimationSequence::new
    );

    public AzAnimationSequence(List<AzAnimationStage> stages) {
        this.stages = stages;
    }

    public List<AzAnimationStage> stages() {
        return stages;
    }
}
