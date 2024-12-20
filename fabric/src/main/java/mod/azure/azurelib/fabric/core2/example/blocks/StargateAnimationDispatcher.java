package mod.azure.azurelib.fabric.core2.example.blocks;

import mod.azure.azurelib.core2.animation.dispatch.AzDispatcher;
import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class StargateAnimationDispatcher {

    private static final String SPIN_ANIMATION_NAME = "spinning";

    private static final AzDispatchCommand SPINNING_COMMAND = AzDispatchCommand.builder()
        .playAnimation("base_controller", SPIN_ANIMATION_NAME)
        .build();

    public void serverSpin(BlockEntity entity) {
        AzDispatcher.fromClient(SPINNING_COMMAND).sendForBlockEntity(entity);
    }
}
