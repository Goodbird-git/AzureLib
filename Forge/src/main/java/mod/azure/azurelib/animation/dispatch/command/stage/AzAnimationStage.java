package mod.azure.azurelib.animation.dispatch.command.stage;

import io.netty.buffer.ByteBuf;
import mod.azure.azurelib.animation.property.AzAnimationStageProperties;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class AzAnimationStage {

    public String name;

    public AzAnimationStageProperties properties;


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

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name);
        properties.toBytes(buf);
    }

    public static AzAnimationStage fromBytes(ByteBuf buf) {
        return new AzAnimationStage(ByteBufUtils.readUTF8String(buf), AzAnimationStageProperties.fromBytes(buf));
    }
}
