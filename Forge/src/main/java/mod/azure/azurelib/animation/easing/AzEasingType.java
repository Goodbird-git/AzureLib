package mod.azure.azurelib.animation.easing;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import mod.azure.azurelib.animation.controller.keyframe.AzAnimationPoint;
import mod.azure.azurelib.animation.dispatch.command.sequence.AzAnimationSequence;
import mod.azure.azurelib.animation.dispatch.command.stage.AzAnimationStage;
import mod.azure.azurelib.core.utils.Interpolations;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface AzEasingType {

    String name();

    Double2DoubleFunction buildTransformer(Double value);


    default double apply(AzAnimationPoint animationPoint) {
        Double easingVariable = null;

        if (animationPoint.keyframe() != null && animationPoint.keyframe().easingArgs().size() > 0)
            easingVariable = animationPoint.keyframe().easingArgs().get(0).get();

        return apply(animationPoint, easingVariable, animationPoint.currentTick() / animationPoint.transitionLength());
    }

    default double apply(AzAnimationPoint animationPoint, Double easingValue, double lerpValue) {
        if (animationPoint.currentTick() >= animationPoint.transitionLength())
            return (float) animationPoint.animationEndValue();

        return Interpolations.lerp(
                animationPoint.animationStartValue(),
                animationPoint.animationEndValue(),
                buildTransformer(easingValue).get(lerpValue)
        );
    }

    default void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name());
    }

    static AzEasingType fromBytes(ByteBuf buf) {
        return Objects.requireNonNull(AzEasingTypeRegistry.getOrNull(ByteBufUtils.readUTF8String(buf)));
    }
}
