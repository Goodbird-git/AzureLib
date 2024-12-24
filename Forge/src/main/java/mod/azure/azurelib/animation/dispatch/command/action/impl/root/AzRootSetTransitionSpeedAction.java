package mod.azure.azurelib.animation.dispatch.command.action.impl.root;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.animation.dispatch.command.action.AzAction;
import net.minecraft.util.ResourceLocation;

public class AzRootSetTransitionSpeedAction implements AzAction {
    public float transitionSpeed;

    public static final StreamCodec<FriendlyByteBuf, AzRootSetTransitionSpeedAction> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            AzRootSetTransitionSpeedAction::transitionSpeed,
            AzRootSetTransitionSpeedAction::new
    );

    public static final ResourceLocation RESOURCE_LOCATION = AzureLib.modResource("root/set_transition_speed");

    public AzRootSetTransitionSpeedAction(float transitionSpeed) {
        this.transitionSpeed = transitionSpeed;
    }

    public float transitionSpeed() {
        return transitionSpeed;
    }

    @Override
    public void handle(AzDispatchSide originSide, AzAnimator<?> animator) {
        animator.getAnimationControllerContainer()
                .getAll()
                .forEach(
                        controller -> controller.setAnimationProperties(
                                controller.animationProperties().withTransitionLength(transitionSpeed)
                        )
                );
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }
}
