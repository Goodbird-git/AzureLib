package mod.azure.azurelib.animation.dispatch.command;

import mod.azure.azurelib.animation.dispatch.command.action.AzDispatchAction;
import mod.azure.azurelib.animation.primitive.AzLoopType;
import mod.azure.azurelib.animation.property.codec.AzListStreamCodec;

import java.util.Collection;
import java.util.List;

/**
 * Represents a command structure used to dispatch a sequence of actions in the animation system. This class primarily
 * serves as a container for a list of {@link AzDispatchAction} instances that define specific operations or behaviors
 * to be executed.
 * <br>
 * The class provides support for building complex dispatch commands by leveraging the hierarchical builder system,
 * enabling customization of animation-related functionality.
 */
public class AzDispatchCommand {

    public List<AzDispatchAction> actions;

    public static final StreamCodec<FriendlyByteBuf, AzDispatchCommand> CODEC = StreamCodec.composite(
            new AzListStreamCodec<>(AzDispatchAction.CODEC),
            AzDispatchCommand::actions,
            AzDispatchCommand::new
    );

    public AzDispatchCommand(List<AzDispatchAction> actions) {
        this.actions = actions;
    }

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
     * @param loopType the loop type for the animation to use
     * @return an instance of {@code AzDispatchCommand} representing the command to play the desired animation
     */
    public static AzDispatchCommand create(String controllerName, String animationName, AzLoopType loopType) {
        return builder()
            .playSequence(
                        controllerName,
                        sequenceBuilder -> sequenceBuilder.queue(animationName, props -> props.withLoopType(loopType))
                )
            .build();
    }

    /**
     * Retrieves the collection of actions encapsulated within this dispatch command. The returned collection consists
     * of {@link AzDispatchAction} instances that define specific operations or behaviors to be executed within the
     * animation system.
     *
     * @return a collection of {@link AzDispatchAction} instances representing the actions associated with this dispatch
     *         command
     */
    public Collection<? extends AzDispatchAction> actions() {
        return actions;
    }
}
