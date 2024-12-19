package mod.azure.azurelib.fabric.core2.example.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import mod.azure.azurelib.core2.animation.dispatch.AzDispatcher;
import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;

public class AzPistolAnimationDispatcher {

    private static final String FIRING_ANIMATION_NAME = "firing";

    private static final AzDispatchCommand FIRING_COMMAND = AzDispatchCommand.builder()
        .playAnimation("base_controller", FIRING_ANIMATION_NAME)
        .build();

    public void serverFire(Entity entity, ItemStack itemStack) {
        AzDispatcher.fromServer(FIRING_COMMAND).sendForItem(entity, itemStack);
    }
}
