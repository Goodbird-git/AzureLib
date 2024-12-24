package mod.azure.azurelib.animation.dispatch.command.action.impl.root;

import io.netty.buffer.ByteBuf;
import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animation.AzAnimator;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.dispatch.AzDispatchSide;
import mod.azure.azurelib.animation.dispatch.command.action.AzAction;
import mod.azure.azurelib.animation.dispatch.command.sequence.AzAnimationSequence;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class AzRootPlayAnimationSequenceAction implements AzAction {
    public String controllerName;
    public AzAnimationSequence sequence;

    public static final ResourceLocation RESOURCE_LOCATION = AzureLib.modResource("root/play_animation_sequence");

    public AzRootPlayAnimationSequenceAction(){

    }

    public AzRootPlayAnimationSequenceAction(String controllerName, AzAnimationSequence sequence) {
        this.controllerName = controllerName;
        this.sequence = sequence;
    }

    public String controllerName() {
        return controllerName;
    }

    public AzAnimationSequence sequence() {
        return sequence;
    }

    @Override
    public void handle(AzDispatchSide originSide, AzAnimator<?> animator) {
        AzAnimationController controller = animator.getAnimationControllerContainer().getOrNull(controllerName);

        if (controller != null) {
            controller.run(originSide, sequence);
        }
    }

    @Override
    public ResourceLocation getResourceLocation() {
        return RESOURCE_LOCATION;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, controllerName);
        sequence.toBytes(buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        controllerName = ByteBufUtils.readUTF8String(buf);
        sequence = AzAnimationSequence.fromBytes(buf);
    }
}
