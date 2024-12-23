package mod.azure.azurelib.fabric.core2.example.blocks;

import net.minecraft.world.level.block.entity.BlockEntity;

import mod.azure.azurelib.core2.animation.dispatch.command.AzCommand;

public class StargateAnimationDispatcher {

    private static final String SPIN_ANIMATION_NAME = "spinning";

    private static final AzCommand SPINNING_COMMAND = AzCommand.create(
        "base_controller",
        SPIN_ANIMATION_NAME
    );

    public void spin(BlockEntity entity) {
        SPINNING_COMMAND.sendForBlockEntity(entity);
    }
}
