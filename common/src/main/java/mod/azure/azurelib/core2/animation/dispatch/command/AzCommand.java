package mod.azure.azurelib.core2.animation.dispatch.command;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

import mod.azure.azurelib.core2.animation.dispatch.command.action.AzAction;
import mod.azure.azurelib.core2.animation.primitive.AzLoopType;
import mod.azure.azurelib.core2.util.codec.AzListStreamCodec;

/**
 * Represents a command structure used to dispatch a sequence of actions in the animation system. This class primarily
 * serves as a container for a list of {@link AzAction} instances that define specific operations or behaviors to be
 * executed. <br>
 * The class provides support for building complex dispatch commands by leveraging the hierarchical builder system,
 * enabling customization of animation-related functionality.
 */
public record AzCommand(List<AzAction> actions) {

    public static final StreamCodec<FriendlyByteBuf, AzCommand> CODEC = StreamCodec.composite(
        new AzListStreamCodec<>(AzAction.CODEC),
        AzCommand::actions,
        AzCommand::new
    );

    public static AzRootCommandBuilder builder() {
        return new AzRootCommandBuilder();
    }

    public static AzCommand create(String controllerName, String animationName) {
        return create(controllerName, animationName, AzLoopType.PLAY_ONCE);
    }

    /**
     * Creates a dispatch command to play a specified animation on a given controller.
     *
     * @param controllerName the name of the animation controller on which the animation should be played
     * @param animationName  the name of the animation to be played on the specified controller
     * @param loopType       the loop type for the animation to use
     * @return an instance of {@code AzCommand} representing the command to play the desired animation
     */
    public static AzCommand create(String controllerName, String animationName, AzLoopType loopType) {
        return builder()
            .playSequence(
                controllerName,
                sequenceBuilder -> sequenceBuilder.queue(animationName, props -> props.withLoopType(loopType))
            )
            .build();
    }
}
