package mod.azure.azurelib.fabric.core2.example.armors;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import mod.azure.azurelib.core2.animation.dispatch.command.AzCommand;

public class AzDoomArmorAnimationDispatcher {

    private static final AzCommand EQUIP = AzCommand.create("base_controller", "equipping");

    public void equip(Entity entity, ItemStack itemStack) {
        EQUIP.sendForItem(entity, itemStack);
    }
}
