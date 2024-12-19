package mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;

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
