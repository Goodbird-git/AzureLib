package mod.azure.azurelib.fabric.core2.example.armors;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class AzDoomArmor extends ArmorItem {

    private final AzDoomArmorAnimationDispatcher dispatcher;

    public AzDoomArmor(Type type) {
        super(ArmorMaterials.NETHERITE, type, new Properties());
        this.dispatcher = new AzDoomArmorAnimationDispatcher();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> swapWithEquipmentSlot(
        Item item,
        Level level,
        Player player,
        InteractionHand hand
    ) {
        var result = super.swapWithEquipmentSlot(item, level, player, hand);
        var itemStack = result.getObject();

        if (!level.isClientSide) {
            // TODO: This dispatch does not work.
            // The reason it doesn't work is because this function finishes before the armor even starts to render.
            // The armor stack recreated on equip, which means the animator associated with it is also destroyed
            // and recreated on equip. To fix this, we need the animators to remain stable even as item stacks change.
            // Item stack references are transient, but their data components are not. So we can map animators
            // by the UUID of the item stack.
            dispatcher.serverEquipHelmet(player, itemStack);
        }

        return result;
    }
}
