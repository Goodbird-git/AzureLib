package mod.azure.azurelib.fabric.core2.example.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import mod.azure.azurelib.core2.animation.dispatch.AzDispatcher;
import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;
import mod.azure.azurelib.core2.animation.primitive.AzLoopType;

public class AzPistolAnimationDispatcher {

    private static final String FIRING_ANIMATION_NAME = "firing";

    private static final AzDispatchCommand FIRING_COMMAND = AzDispatchCommand.create(
        "base_controller",
        FIRING_ANIMATION_NAME,
        AzLoopType.PLAY_ONCE
    );

    public void serverFire(Entity entity, ItemStack itemStack) {
        AzDispatcher.fromServer(FIRING_COMMAND).sendForItem(entity, itemStack);
    }
}
