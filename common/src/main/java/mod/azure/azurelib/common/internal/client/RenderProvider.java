/**
 * This class is a fork of the matching class found in the Configuration repository.
 * Original source: https://github.com/Toma1O6/Configuration
 * Copyright © 2024 Toma1O6.
 * Licensed under the MIT License.
 */
 package mod.azure.azurelib.common.internal.client;

import mod.azure.azurelib.common.api.common.animatable.GeoItem;
import mod.azure.azurelib.common.internal.mixins.ItemRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Internal interface for safely providing a custom renderer instances at runtime.<br>
 * This can be safely instantiated as a new anonymous class inside your {@link Item} class
 */
public interface RenderProvider {

    RenderProvider DEFAULT = new RenderProvider() {
    };

    static RenderProvider of(ItemStack itemStack) {
        return of(itemStack.getItem());
    }

    static RenderProvider of(Item item) {
        if (item instanceof GeoItem geoItem) {
            return (RenderProvider) geoItem.getRenderProvider().get();
        }

        return DEFAULT;
    }

    default BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return ((ItemRendererAccessor) Minecraft.getInstance().getItemRenderer()).getBlockEntityRenderer();
    }

    default Model getGenericArmorModel(
            LivingEntity livingEntity,
            ItemStack itemStack,
            EquipmentSlot equipmentSlot,
            HumanoidModel<LivingEntity> original
    ) {
        HumanoidModel<LivingEntity> replacement = getHumanoidArmorModel(
                livingEntity,
                itemStack,
                equipmentSlot,
                original
        );

        if (replacement != original) {
            original.copyPropertiesTo(replacement);
            return replacement;
        }

        return original;
    }

    default HumanoidModel<LivingEntity> getHumanoidArmorModel(
            LivingEntity livingEntity,
            ItemStack itemStack,
            EquipmentSlot equipmentSlot,
            HumanoidModel<LivingEntity> original
    ) {
        return original;
    }
}
