package mod.azure.azurelib.animatable.client;

import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.loading.json.raw.Model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Internal interface for safely providing a custom renderer instances at runtime.<br>
 * This can be safely instantiated as a new anonymous class inside your {@link Item} class
 */
public interface RenderProvider {
    RenderProvider DEFAULT = new RenderProvider() {};

    static RenderProvider of(ItemStack itemStack) {
        return of(itemStack.getItem());
    }

    static RenderProvider of(Item item) {
        if (item instanceof GeoItem) {
            return (RenderProvider) ((GeoItem) item).getRenderProvider().get();
        }

        return DEFAULT;
    }

    default TileEntityItemStackRenderer getCustomRenderer() {
        return TileEntityItemStackRenderer.instance;
    }

    default ModelBiped getGenericArmorModel(EntityLivingBase livingEntity, ItemStack itemStack, EntityEquipmentSlot equipmentSlot, ModelBiped original) {
        ModelBiped replacement = getHumanoidArmorModel(livingEntity, itemStack, equipmentSlot, original);

        if (replacement != original) {
            original.setModelAttributes(replacement);
            return replacement;
        }

        return original;
    }

    default ModelBiped getHumanoidArmorModel(EntityLivingBase livingEntity, ItemStack itemStack, EntityEquipmentSlot equipmentSlot, ModelBiped original) {
        return original;
    }
}