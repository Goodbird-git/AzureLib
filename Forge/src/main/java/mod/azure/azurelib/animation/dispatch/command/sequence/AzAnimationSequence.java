package mod.azure.azurelib.animation.dispatch.command.sequence;

import io.netty.buffer.ByteBuf;
import mod.azure.azurelib.animation.dispatch.command.stage.AzAnimationStage;

import java.util.ArrayList;
import java.util.List;

public class AzAnimationSequence {

    public List<AzAnimationStage> stages;

    public AzAnimationSequence(List<AzAnimationStage> stages) {
        this.stages = stages;
    }

    public List<AzAnimationStage> stages() {
        return stages;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeByte(stages.size());
        stages.forEach(element -> element.toBytes(buf));
    }

    public static AzAnimationSequence fromBytes(ByteBuf buf) {
        byte size = buf.readByte();
        List<AzAnimationStage> stages = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            stages.add(AzAnimationStage.fromBytes(buf));
        }
        return new AzAnimationSequence(stages);
    }
}
