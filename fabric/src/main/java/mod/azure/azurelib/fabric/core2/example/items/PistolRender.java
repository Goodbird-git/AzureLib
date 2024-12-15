package mod.azure.azurelib.fabric.core2.example.items;

import mod.azure.azurelib.common.api.client.model.DefaultedItemGeoModel;
import mod.azure.azurelib.common.api.client.renderer.GeoItemRenderer;
import mod.azure.azurelib.common.internal.common.AzureLib;

public class PistolRender extends GeoItemRenderer<Pistol> {

    public PistolRender() {
        super(new DefaultedItemGeoModel<>(AzureLib.modResource("pistol")));
    }
}
