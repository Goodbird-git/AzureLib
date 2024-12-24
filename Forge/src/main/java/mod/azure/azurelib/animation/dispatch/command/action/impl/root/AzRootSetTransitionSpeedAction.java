package mod.azure.azurelib.animation.dispatch.command.action.impl.root;

import io.netty.buffer.ByteBuf;
import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.animation.dispatch.command.action.AzAction;
import net.minecraft.util.ResourceLocation;

public class AzRootSetTransitionSpeedAction implements AzAction {
    public float transitionSpeed;


    public static final ResourceLocation RESOURCE_LOCATION = AzureLib.modResource("root/set_transition_speed");

    public AzRootSetTransitionSpeedAction(){

    }

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

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(transitionSpeed);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        transitionSpeed = buf.readFloat();
    }
}
