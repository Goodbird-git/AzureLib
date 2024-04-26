package mod.azure.azurelib.common.api.common.enchantments;

import mod.azure.azurelib.common.api.common.tags.AzureTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class IncendiaryEnchantment extends Enchantment {

    public IncendiaryEnchantment(EquipmentSlot... slots) {
        super(Enchantment.definition(
                AzureTags.GUNS, 2, 3, Enchantment.dynamicCost(10, 10), Enchantment.dynamicCost(40, 10), 4, slots));
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.is(AzureTags.GUNS);
    }
}
