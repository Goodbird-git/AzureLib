package mod.azure.azurelib.core2.animation.dispatch.command;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;
import mod.azure.azurelib.core2.animation.primitive.AzLoopType;
import mod.azure.azurelib.core2.util.codec.AzListStreamCodec;

/**
 * Represents a command structure used to dispatch a sequence of actions in the animation system. This class primarily
 * serves as a container for a list of {@link AzDispatchAction} instances that define specific operations or behaviors
 * to be executed. <br>
 * The class provides support for building complex dispatch commands by leveraging the hierarchical builder system,
 * enabling customization of animation-related functionality.
 */
public record AzDispatchCommand(List<AzDispatchAction> actions) {

    public static final StreamCodec<FriendlyByteBuf, AzDispatchCommand> CODEC = StreamCodec.composite(
        new AzListStreamCodec<>(AzDispatchAction.CODEC),
        AzDispatchCommand::actions,
        AzDispatchCommand::new
    );

    public static AzDispatchRootCommandBuilder builder() {
        return new AzDispatchRootCommandBuilder();
    }

    public static AzDispatchCommand create(String controllerName, String animationName) {
        return create(controllerName, animationName, AzLoopType.PLAY_ONCE);
    }

    /**
     * Creates a dispatch command to play a specified animation on a given controller.
     *
     * @param controllerName the name of the animation controller on which the animation should be played
     * @param animationName  the name of the animation to be played on the specified controller
     * @param loopType       the loop type for the animation to use
     * @return an instance of {@code AzDispatchCommand} representing the command to play the desired animation
     */
    public static AzDispatchCommand create(String controllerName, String animationName, AzLoopType loopType) {
        return builder()
            .play(controllerName, animationName)
            .playSequence(
                controllerName,
                sequenceBuilder -> sequenceBuilder.queue(animationName, props -> props.setLoopType(loopType))
            )
            .build();
    }
}
