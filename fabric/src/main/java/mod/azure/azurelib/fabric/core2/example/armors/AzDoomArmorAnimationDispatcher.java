package mod.azure.azurelib.fabric.core2.example.armors;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import mod.azure.azurelib.core2.animation.dispatch.AzDispatcher;
import mod.azure.azurelib.core2.animation.dispatch.command.AzDispatchCommand;

public class AzDoomArmorAnimationDispatcher {

    private static final AzDispatchCommand EQUIP = AzDispatchCommand.create("base_controller", "equipping");

    public void serverEquipHelmet(Entity entity, ItemStack itemStack) {
        AzDispatcher.fromServer(EQUIP).sendForItem(entity, itemStack);
    }
}
