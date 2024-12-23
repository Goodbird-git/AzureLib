package mod.azure.azurelib.fabric.core2.example.blocks;

import net.minecraft.world.level.block.entity.BlockEntity;

import mod.azure.azurelib.core2.animation.dispatch.AzDispatcher;
import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;

public class StargateAnimationDispatcher {

    private static final String SPIN_ANIMATION_NAME = "spinning";

    private static final AzDispatchCommand SPINNING_COMMAND = AzDispatchCommand.create(
        "base_controller",
        SPIN_ANIMATION_NAME
    );

    public void serverSpin(BlockEntity entity) {
        AzDispatcher.fromClient(SPINNING_COMMAND).sendForBlockEntity(entity);
    }
}
