package mod.azure.azurelib.animation.dispatch.command.action.impl.root;

import io.netty.buffer.ByteBuf;
import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.animation.dispatch.command.action.AzAction;
import mod.azure.azurelib.animation.easing.AzEasingType;
import net.minecraft.util.ResourceLocation;

public class AzRootSetEasingTypeAction implements AzAction {
    public AzEasingType easingType;

    public AzRootSetEasingTypeAction(){

    }

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

    @Override
    public void toBytes(ByteBuf buf) {
        easingType.toBytes(buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        easingType = AzEasingType.fromBytes(buf);
    }
}
