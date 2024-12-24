package mod.azure.azurelib.animation.dispatch.command.action.impl.root;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.animation.dispatch.command.action.AzAction;
import mod.azure.azurelib.animation.easing.AzEasingType;
import net.minecraft.util.ResourceLocation;

public class AzRootSetEasingTypeAction implements AzAction {
    public AzEasingType easingType;

    public static final StreamCodec<FriendlyByteBuf, AzRootSetEasingTypeAction> CODEC = StreamCodec.composite(
            AzEasingType.STREAM_CODEC,
            AzRootSetEasingTypeAction::easingType,
            AzRootSetEasingTypeAction::new
    );

    public AzRootSetEasingTypeAction(AzEasingType easingType) {
        this.easingType = easingType;
    }

    public AzEasingType easingType() {
        return easingType;
    }

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
