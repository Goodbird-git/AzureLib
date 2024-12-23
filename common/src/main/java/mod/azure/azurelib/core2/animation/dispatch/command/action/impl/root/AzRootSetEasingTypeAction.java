package mod.azure.azurelib.core2.animation.dispatch.command.action.impl.root;

import mod.azure.azurelib.core2.animation.dispatch.AzDispatchSide;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.core2.animation.AzAnimator;
import mod.azure.azurelib.core2.animation.dispatch.command.action.AzDispatchAction;
import mod.azure.azurelib.core2.animation.easing.AzEasingType;

public record AzRootSetEasingTypeAction(
    AzEasingType easingType
) implements AzDispatchAction {

    public static final StreamCodec<FriendlyByteBuf, AzRootSetEasingTypeAction> CODEC = StreamCodec.composite(
        AzEasingType.STREAM_CODEC,
        AzRootSetEasingTypeAction::easingType,
        AzRootSetEasingTypeAction::new
    );

    public static final ResourceLocation RESOURCE_LOCATION = AzureLib.modResource("root/set_easing_type");

    @Override
    public void handle(AzDispatchSide originSide, AzAnimator<?> animator) {
        animator.getAnimationControllerContainer()
            .getAll()
            .forEach(
                controller -> controller.setAnimationProperties(
                    controller.animationProperties().withEasingType(easingType)
                )
            );
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }
}
