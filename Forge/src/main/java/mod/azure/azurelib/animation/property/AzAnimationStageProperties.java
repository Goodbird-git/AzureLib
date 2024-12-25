package mod.azure.azurelib.animation.property;

import io.netty.buffer.ByteBuf;
import mod.azure.azurelib.animation.easing.AzEasingType;
import mod.azure.azurelib.animation.easing.AzEasingTypeRegistry;
import mod.azure.azurelib.animation.easing.AzEasingTypes;
import mod.azure.azurelib.animation.primitive.AzLoopType;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.Objects;

public class AzAnimationStageProperties extends AzAnimationProperties {


    public static final AzAnimationStageProperties DEFAULT = new AzAnimationStageProperties(
            1D,
            AzEasingTypes.NONE,
            AzLoopType.PLAY_ONCE,
            0F
    );

    public static final AzAnimationStageProperties EMPTY = new AzAnimationStageProperties(null, null, null, null);

    private final AzLoopType loopType;

    public AzAnimationStageProperties(
            Double animationSpeed,
            AzEasingType easingType,
            AzLoopType loopType,
            Float transitionLength
    ) {
        super(animationSpeed, easingType, transitionLength);
        this.loopType = loopType;
    }

    public boolean hasLoopType() {
        return loopType != null;
    }

    @Override
    public AzAnimationStageProperties withAnimationSpeed(double animationSpeed) {
        return new AzAnimationStageProperties(animationSpeed, easingType, loopType, transitionLength);
    }

    @Override
    public AzAnimationStageProperties withEasingType(AzEasingType easingType) {
        return new AzAnimationStageProperties(animationSpeed, easingType, loopType, transitionLength);
    }

    public AzAnimationStageProperties withLoopType(AzLoopType loopType) {
        return new AzAnimationStageProperties(animationSpeed, easingType, loopType, transitionLength);
    }

    @Override
    public AzAnimationStageProperties withTransitionLength(float transitionLength) {
        return new AzAnimationStageProperties(animationSpeed, easingType, loopType, transitionLength);
    }

    public AzLoopType loopType() {
        return loopType == null ? DEFAULT.loopType() : loopType;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        if (!super.equals(object)) {
            return false;
        }

        AzAnimationStageProperties that = (AzAnimationStageProperties) object;

        return Objects.equals(loopType, that.loopType) && super.equals(object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), loopType);
    }

    public void toBytes(ByteBuf buf) {
        byte propertyLength = 0;
        propertyLength += (byte) (hasAnimationSpeed() ? 1 : 0);
        propertyLength += (byte) (hasTransitionLength() ? 1 : 0);
        propertyLength += (byte) (hasEasingType() ? 1 : 0);
        propertyLength += (byte) (hasLoopType() ? 1 : 0);

        buf.writeByte(propertyLength);

        if (hasAnimationSpeed()) {
            buf.writeByte(0);
            buf.writeDouble(animationSpeed());
        }

        if (hasTransitionLength()) {
            buf.writeByte(1);
            buf.writeFloat(transitionLength());
        }

        if (hasEasingType()) {
            buf.writeByte(2);
            ByteBufUtils.writeUTF8String(buf, easingType().name());
        }

        if (hasLoopType()) {
            buf.writeByte(3);
            ByteBufUtils.writeUTF8String(buf, loopType().name());
        }
    }

    public static AzAnimationStageProperties fromBytes(ByteBuf buf) {
        byte propertyLength = buf.readByte();
        AzAnimationStageProperties properties = AzAnimationStageProperties.EMPTY;

        for (int i = 0; i < propertyLength; i++) {
            byte code = buf.readByte();

            if(code==0){
                properties = properties.withAnimationSpeed(buf.readDouble());
            }else if(code==1){
                properties = properties.withTransitionLength(buf.readFloat());
            }else if(code==2){
                AzEasingType easingType = AzEasingTypeRegistry.getOrDefault(ByteBufUtils.readUTF8String(buf), AzEasingTypes.NONE);
                properties = properties.withEasingType(easingType);
            }else if(code==3){
                AzLoopType loopType = AzLoopType.fromString(ByteBufUtils.readUTF8String(buf));
                properties = properties.withLoopType(loopType);
            }
        }
        return properties;
    }
}