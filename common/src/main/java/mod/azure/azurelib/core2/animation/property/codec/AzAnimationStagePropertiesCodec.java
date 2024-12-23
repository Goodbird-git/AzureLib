package mod.azure.azurelib.core2.animation.property.codec;

import mod.azure.azurelib.core2.animation.easing.AzEasingTypeRegistry;
import mod.azure.azurelib.core2.animation.easing.AzEasingTypes;
import mod.azure.azurelib.core2.animation.primitive.AzLoopType;
import mod.azure.azurelib.core2.animation.property.AzAnimationStageProperties;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class AzAnimationStagePropertiesCodec implements StreamCodec<FriendlyByteBuf, AzAnimationStageProperties> {

    @Override
    public @NotNull AzAnimationStageProperties decode(FriendlyByteBuf buf) {
        var propertyLength = buf.readByte();
        var properties = AzAnimationStageProperties.EMPTY;

        for (int i = 0; i < propertyLength; i++) {
            var code = buf.readByte();

            switch (code) {
                case 0 -> properties = properties.withAnimationSpeed(buf.readDouble());
                case 1 -> properties = properties.withTransitionLength(buf.readFloat());
                case 2 -> {
                    var easingType = AzEasingTypeRegistry.getOrDefault(buf.readUtf(), AzEasingTypes.NONE);
                    properties = properties.withEasingType(easingType);
                }
                case 3 -> {
                    var loopType = AzLoopType.fromString(buf.readUtf());
                    properties = properties.withLoopType(loopType);
                }
            }
        }

        return properties;
    }

    @Override
    public void encode(FriendlyByteBuf buf, AzAnimationStageProperties properties) {
        var propertyLength = 0;
        propertyLength += properties.hasAnimationSpeed() ? 1 : 0;
        propertyLength += properties.hasTransitionLength() ? 1 : 0;
        propertyLength += properties.hasEasingType() ? 1 : 0;

        buf.writeByte(propertyLength);

        if (properties.hasAnimationSpeed()) {
            buf.writeByte(0);
            buf.writeDouble(properties.animationSpeed());
        }

        if (properties.hasTransitionLength()) {
            buf.writeByte(1);
            buf.writeFloat(properties.transitionLength());
        }

        if (properties.hasEasingType()) {
            buf.writeByte(2);
            buf.writeUtf(properties.easingType().name());
        }

        if (properties.hasLoopType()) {
            buf.writeByte(3);
            buf.writeUtf(properties.loopType().name());
        }
    }
}
