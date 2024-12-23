package mod.azure.azurelib.core2.animation.dispatch.command.sequence;

import mod.azure.azurelib.core2.animation.dispatch.command.stage.AzAnimationStage;
import mod.azure.azurelib.core2.util.codec.AzListStreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record AzAnimationSequence(
    List<AzAnimationStage> stages
) {

    public static final StreamCodec<FriendlyByteBuf, AzAnimationSequence> CODEC = StreamCodec.composite(
        new AzListStreamCodec<>(AzAnimationStage.CODEC),
        AzAnimationSequence::stages,
        AzAnimationSequence::new
    );
}
