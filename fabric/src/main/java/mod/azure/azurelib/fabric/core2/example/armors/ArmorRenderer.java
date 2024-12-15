package mod.azure.azurelib.fabric.core2.example.armors;

import net.minecraft.world.item.ArmorItem;

import mod.azure.azurelib.common.api.client.model.DefaultedItemGeoModel;
import mod.azure.azurelib.common.api.client.renderer.GeoArmorRenderer;
import mod.azure.azurelib.common.internal.common.AzureLib;
import mod.azure.azurelib.common.internal.common.cache.object.GeoBone;

public class ArmorRenderer<D extends ArmorItem> extends GeoArmorRenderer<DoomArmor> {

    public ArmorRenderer() {
        super(new DefaultedItemGeoModel<>(AzureLib.modResource("doomicorn")));
    }

    // Only models I have on for armors with animations, the arm/legs are named wrong and i just didnt want to load it
    // in bb again to rename, this should be a feature kept though
    @Override
    public GeoBone getLeftBootBone() {
        return this.model.getBone("armorRightBoot").orElse(null);
    }

    @Override
    public GeoBone getLeftLegBone() {
        return this.model.getBone("armorRightLeg").orElse(null);
    }

    @Override
    public GeoBone getRightBootBone() {
        return this.model.getBone("armorLeftBoot").orElse(null);
    }

    @Override
    public GeoBone getRightLegBone() {
        return this.model.getBone("armorLeftLeg").orElse(null);
    }
}
