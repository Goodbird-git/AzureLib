package mod.azure.azurelib.animation.dispatch.command.stage;

import mod.azure.azurelib.animation.property.AzAnimationStageProperties;

public class AzAnimationStage {

    public String name;

    public AzAnimationStageProperties properties;

    public static final StreamCodec<FriendlyByteBuf, AzAnimationStage> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            AzAnimationStage::name,
            AzAnimationStageProperties.CODEC,
            AzAnimationStage::properties,
            AzAnimationStage::new
    );

    public AzAnimationStage(String name, AzAnimationStageProperties properties) {
        this.name = name;
        this.properties = properties;
    }

    public String name() {
        return name;
    }

    public AzAnimationStageProperties properties() {
        return properties;
    }
}
