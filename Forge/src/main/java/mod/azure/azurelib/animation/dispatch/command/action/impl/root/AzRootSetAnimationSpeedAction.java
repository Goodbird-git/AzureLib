package mod.azure.azurelib.animation.dispatch.command.action.impl.root;

import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.animation.dispatch.command.action.AzAction;
import net.minecraft.util.ResourceLocation;

public class AzRootSetAnimationSpeedAction implements AzAction {

    public double animationSpeed;

    public AzRootSetAnimationSpeedAction(double animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public double animationSpeed() {
        return animationSpeed;
    }

    public static final StreamCodec<FriendlyByteBuf, AzRootSetAnimationSpeedAction> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE,
            AzRootSetAnimationSpeedAction::animationSpeed,
            AzRootSetAnimationSpeedAction::new
    );

    public static final ResourceLocation RESOURCE_LOCATION = AzureLib.modResource("root/set_animation_speed");

    @Override
    public void handle(AzDispatchSide originSide, AzAnimator<?> animator) {
        animator.getAnimationControllerContainer()
                .getAll()
                .forEach(
                        controller -> controller.setAnimationProperties(
                                controller.animationProperties().withAnimationSpeed(animationSpeed)
                        )
                );
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }
}
