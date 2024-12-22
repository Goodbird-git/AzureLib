package mod.azure.azurelib.animation.dispatch.command.action.impl.root;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.dispatch.command.action.AzDispatchAction;
import net.minecraft.util.ResourceLocation;

/**
 * Represents an action that cancels the current animation of a specified animation controller in the animation system.
 * This action is part of the root-level dispatch actions and interacts with a specific animation controller by name.
 * <br/>
 * <br/>
 * An instance of this record is serialized and deserialized using the {@code CODEC}, and it is associated with a unique
 * resource location defined by {@code RESOURCE_LOCATION}. <br/>
 * <br/>
 * When executed, the {@code handle} method ensures that the animation of the targeted controller is stopped by setting
 * its current animation to {@code null}. <br/>
 * <br/>
 * This class is primarily used within the {@code AzAnimator} context where each animation controller is part of the
 * animator's controller container. <br/>
 * <br/>
 * Implements: - {@link AzDispatchAction}: Allows the action to be dispatched within the animation system. <br/>
 * <br/>
 * Fields:
 * <ul>
 * <li>{@code controllerName}: The name of the animation controller which this action targets.</li>
 * </ul>
 * <br/>
 * <br/>
 * Constants:
 * <ul>
 * <li>{@code CODEC}: A codec for encoding and decoding this action during network communication.</li>
 * <li>{@code RESOURCE_LOCATION}: A unique identifier for this action.</li>
 * </ul>
 * <br/>
 * <br/>
 * Methods:
 * <ul>
 * <li>{@code handle(AzAnimator<?> animator)}: Stops the current animation of the specified controller within the
 * animator's animation controller container.</li>
 * <li>{@code getResourceLocation()}: Returns the unique resource location associated with this action.</li>
 * </ul>
 */
public class AzRootCancelAction implements AzDispatchAction {
    public String controllerName;

    public AzRootCancelAction(String controllerName) {
        this.controllerName = controllerName;
    }

    public String controllerName() {
        return controllerName;
    }
    public static final StreamCodec<FriendlyByteBuf, AzRootCancelAction> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        AzRootCancelAction::controllerName,
        AzRootCancelAction::new
    );

    public static final ResourceLocation RESOURCE_LOCATION = AzureLib.modResource("root/cancel");

    @Override
    public void handle(AzAnimator<?> animator) {
        AzAnimationController controller = animator.getAnimationControllerContainer().getOrNull(controllerName);

        if (controller != null) {
            controller.setCurrentAnimation(null);
        }
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }
}
