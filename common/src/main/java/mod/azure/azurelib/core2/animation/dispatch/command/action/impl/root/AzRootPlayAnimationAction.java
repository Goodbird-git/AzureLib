package mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;

/**
 * The AzRootPlayAnimationAction record represents a dispatchable action within the animation system that is used to
 * trigger a specific animation on an animation controller. It specifies the target animation controller by its name and
 * the animation to be played there. <br>
 * This action is implemented as part of the AzDispatchAction system, which provides methods to handle the action within
 * an animator context and retrieve its associated resource location. <br>
 * This record utilizes a predefined codec to allow serialization and deserialization of its attributes, enabling it to
 * be seamlessly used in networked environments or saved states.
 */
public record AzRootPlayAnimationAction(
    String controllerName,
    String animationName
) implements AzDispatchAction {

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
        var controller = animator.getAnimationControllerContainer().getOrNull(controllerName);

        if (controller != null) {
            controller.tryTriggerAnimation(animationName);
        }
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }
}
