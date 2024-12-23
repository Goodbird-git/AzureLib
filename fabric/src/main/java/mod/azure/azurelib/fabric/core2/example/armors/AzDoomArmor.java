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

        if (!level.isClientSide) {
            var slot = getEquipmentSlot();
            var itemStack = player.getItemBySlot(slot);
            dispatcher.serverEquipHelmet(player, itemStack);
        }

        return result;
    }
}
