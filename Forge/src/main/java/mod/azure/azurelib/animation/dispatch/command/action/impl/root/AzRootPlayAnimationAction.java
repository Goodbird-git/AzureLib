package mod.azure.azurelib.animation.dispatch.command.action.impl.root;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.dispatch.command.action.AzDispatchAction;
import net.minecraft.util.ResourceLocation;

/**
 * The AzRootPlayAnimationAction record represents a dispatchable action within the animation system that is used to
 * trigger a specific animation on an animation controller. It specifies the target animation controller by its name and
 * the animation to be played there.
 * <br>
 * This action is implemented as part of the AzDispatchAction system, which provides methods to handle the action within
 * an animator context and retrieve its associated resource location.
 * <br>
 * This record utilizes a predefined codec to allow serialization and deserialization of its attributes, enabling it to
 * be seamlessly used in networked environments or saved states.
 */
public class AzRootPlayAnimationAction implements AzDispatchAction {

    public String controllerName;
    public String animationName;

    public AzRootPlayAnimationAction(String controllerName, String animationName) {
        this.controllerName = controllerName;
        this.animationName = animationName;
    }

    public String controllerName() {
        return controllerName;
    }

    public String animationName() {
        return animationName;
    }

    public static final StreamCodec<FriendlyByteBuf, AzRootPlayAnimationAction> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        AzRootPlayAnimationAction::controllerName,
        ByteBufCodecs.STRING_UTF8,
        AzRootPlayAnimationAction::animationName,
        AzRootPlayAnimationAction::new
    );

    public static final ResourceLocation RESOURCE_LOCATION = AzureLib.modResource("root/play_animation");

    @Override
    public void handle(AzAnimator<?> animator) {
        AzAnimationController controller = animator.getAnimationControllerContainer().getOrNull(controllerName);

        if (controller != null) {
            controller.tryTriggerAnimation(animationName);
        }
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }
}
