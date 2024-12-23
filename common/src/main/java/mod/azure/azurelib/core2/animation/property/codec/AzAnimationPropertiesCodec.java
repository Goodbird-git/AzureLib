package mod.azure.azurelib.core2.animation.property.codec;

import mod.azure.azurelib.core2.animation.easing.AzEasingTypeRegistry;
import mod.azure.azurelib.core2.animation.easing.AzEasingTypes;
import mod.azure.azurelib.core2.animation.property.AzAnimationProperties;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class AzAnimationPropertiesCodec implements StreamCodec<FriendlyByteBuf, AzAnimationProperties> {

    @Override
    public @NotNull AzAnimationProperties decode(FriendlyByteBuf buf) {
        var propertyLength = buf.readByte();
        var properties = AzAnimationProperties.EMPTY;

        for (int i = 0; i < propertyLength; i++) {
            var code = buf.readByte();

            switch (code) {
                case 0 -> properties = properties.withAnimationSpeed(buf.readDouble());
                case 1 -> properties = properties.withTransitionLength(buf.readFloat());
                case 2 -> {
                    var easingType = AzEasingTypeRegistry.getOrDefault(buf.readUtf(), AzEasingTypes.NONE);
                    properties = properties.withEasingType(easingType);
                }
            }
        }

        return properties;
    }

    @Override
    public void encode(FriendlyByteBuf buf, AzAnimationProperties properties) {
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
    }
}
