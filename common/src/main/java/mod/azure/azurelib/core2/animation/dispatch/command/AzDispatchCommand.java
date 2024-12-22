package mod.azure.azurelib.core2.animation.dispatch.command;

import java.util.Collection;
import java.util.List;

import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;
import mod.azure.azurelib.core2.animation.dispatch.command.action.codec.AzDispatchCommandCodec;

/**
 * Represents a command structure used to dispatch a sequence of actions in the animation system. This class primarily
 * serves as a container for a list of {@link AzDispatchAction} instances that define specific operations or behaviors
 * to be executed.
 * <br>
 * The class provides support for building complex dispatch commands by leveraging the hierarchical builder system,
 * enabling customization of animation-related functionality.
 */
public class AzDispatchCommand {

    public static final AzDispatchCommandCodec CODEC = new AzDispatchCommandCodec();

    private final List<AzDispatchAction> actions;

    public AzDispatchCommand(List<AzDispatchAction> actions) {
        this.actions = actions;
    }

    public static AzDispatchRootCommandBuilder builder() {
        return new AzDispatchRootCommandBuilder();
    }

    /**
     * Creates a dispatch command to play a specified animation on a given controller.
     *
     * @param controllerName the name of the animation controller on which the animation should be played
     * @param animationName  the name of the animation to be played on the specified controller
     * @return an instance of {@code AzDispatchCommand} representing the command to play the desired animation
     */
    public static AzDispatchCommand playAnimation(String controllerName, String animationName) {
        return builder()
            .playAnimation(controllerName, animationName)
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
    public Collection<? extends AzDispatchAction> getActions() {
        return actions;
    }
}
